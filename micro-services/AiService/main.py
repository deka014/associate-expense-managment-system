from fastapi import FastAPI, HTTPException, UploadFile, File
from pydantic import BaseModel
from langchain_openai import ChatOpenAI
from langchain_core.messages import HumanMessage, SystemMessage
import os
import json
import re
from dotenv import load_dotenv
from typing import List, Optional

import vector_store
import fraud_detector

# Load environment variables from the .env file one level up (in the micro-services directory)
load_dotenv(dotenv_path="../.env")

app = FastAPI(title="AI Agent Service", version="1.0.0")

# Setup OpenRouter Client
# Ensure you set the OPENROUTER_API_KEY environment variable in docker-compose.yml or your .env file
OPENROUTER_API_KEY = os.getenv("OPENROUTER_API_KEY", "your-api-key-here")

try:
    chat_model = ChatOpenAI(
        model="openai/gpt-oss-120b:free",  # Standard free model on OpenRouter
        openai_api_key=OPENROUTER_API_KEY,
        openai_api_base="https://openrouter.ai/api/v1",
        default_headers={
            "HTTP-Referer": "http://localhost:8000",  # Required by OpenRouter
            "X-Title": "Expense Management System"
        }
    )
except Exception as e:
    print(f"Failed to initialize OpenRouter: {e}")
    chat_model = None

class ChatRequest(BaseModel):
    message: str

class ChatResponse(BaseModel):
    response: str

class ExpenseValidationRequest(BaseModel):
    amount: float
    category: str
    description: str

class ExpenseValidationResponse(BaseModel):
    compliant: bool
    reason: str

class ExpenseAuditItem(BaseModel):
    id: int
    amount: float
    category: str
    date: Optional[str] = "N/A"
    description: Optional[str] = "N/A"
    receipt_base64: Optional[str] = None

class AuditRequest(BaseModel):
    expenses: List[ExpenseAuditItem]

class AuditResultItem(BaseModel):
    expense_id: int
    flags: List[str]
    reason: str
    status: str

class AuditResponse(BaseModel):
    audit_results: List[AuditResultItem]

@app.get("/ai/health")
def health_check():
    return {"status": "up"}

@app.post("/ai/chat", response_model=ChatResponse)
def chat_with_agent(request: ChatRequest):
    if not chat_model:
        raise HTTPException(status_code=500, detail="LLM model not initialized")
    
    try:
        messages = [
            SystemMessage(content="You are a helpful AI assistant for an Expense Management System."),
            HumanMessage(content=request.message)
        ]
        response = chat_model.invoke(messages)
        return ChatResponse(response=str(response.content))
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/ai/upload-policy")
async def upload_policy(file: UploadFile = File(...)):
    """Uploads and indexes the company expense policy PDF into ChromaDB."""
    if not file.filename or not file.filename.endswith(".pdf"):
        raise HTTPException(status_code=400, detail="Only PDF files are supported.")
    
    try:
        content = await file.read()
        vector_store.add_policy_document(content, file.filename)
        return {"message": f"Policy document '{file.filename}' successfully processed and indexed."}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error indexing policy: {str(e)}")

@app.post("/ai/validate-expense", response_model=ExpenseValidationResponse)
def validate_expense(request: ExpenseValidationRequest):
    """Checks a single expense request against the company policy document using RAG."""
    if not chat_model:
        raise HTTPException(status_code=500, detail="LLM model not initialized")
        
    # Search vector store for relevant policies
    query_str = f"Category: {request.category}, Amount: {request.amount}, Description: {request.description}"
    try:
        policy_contexts = vector_store.query_policy(query_str, k=3)
    except Exception as e:
        policy_contexts = []
        print(f"Error querying policy vector store: {e}")
        
    context_text = "\n---\n".join(policy_contexts) if policy_contexts else "No explicit policy documents uploaded. Apply general business expense rules (e.g., standard categories, reasonable amounts)."
    
    system_prompt = (
        "You are an AI Compliance Officer. Your task is to validate a submitted expense request against the retrieved company policy text.\n"
        "Return a JSON response in the following format:\n"
        "{\n"
        "  \"compliant\": true or false,\n"
        "  \"reason\": \"A concise reason citing the policy section or explaining compliance/violation\"\n"
        "}\n"
        "If no policy documents exist, default to checking if the expense description and amount seem realistic and reasonable."
    )
    
    user_prompt = f"""
Retrieved Policy Excerpts:
{context_text}

Submitted Expense details:
- Amount: {request.amount}
- Category: {request.category}
- Description: {request.description}
"""

    try:
        messages = [
            SystemMessage(content=system_prompt),
            HumanMessage(content=user_prompt)
        ]
        response = chat_model.invoke(messages)
        res_content = str(response.content)
        
        # Parse JSON
        parsed = {"compliant": True, "reason": "No policy violations identified."}
        match = re.search(r"\{.*\}", res_content, re.DOTALL)
        if match:
            try:
                parsed = json.loads(match.group(0))
            except Exception:
                pass
        else:
            try:
                parsed = json.loads(res_content)
            except Exception:
                pass
                
        # Handle string booleans
        compliant_val = parsed.get("compliant")
        if isinstance(compliant_val, str):
            compliant_val = compliant_val.lower() == "true"
        elif compliant_val is None:
            compliant_val = True
            
        return ExpenseValidationResponse(
            compliant=compliant_val,
            reason=str(parsed.get("reason", "Verified against available company policies."))
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"LLM verification failed: {str(e)}")

@app.post("/ai/audit", response_model=AuditResponse)
def audit_expenses(request: AuditRequest):
    """Audits a batch of expenses, cross-referencing each with its uploaded receipt PDF."""
    results = []
    for expense in request.expenses:
        audit_res = fraud_detector.audit_single_expense(expense.model_dump(), chat_model)
        results.append(AuditResultItem(**audit_res))
    return AuditResponse(audit_results=results)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
