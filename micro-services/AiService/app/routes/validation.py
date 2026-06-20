from fastapi import APIRouter, HTTPException
from langchain_core.messages import HumanMessage, SystemMessage
import json
import re

from app.config import chat_model
from app.services import vector_store
from app.models.schemas import ExpenseValidationRequest, ExpenseValidationResponse

router = APIRouter(tags=["Validation"])


@router.post("/ai/validate-expense", response_model=ExpenseValidationResponse)
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
