from fastapi import APIRouter, HTTPException, UploadFile, File
from langchain_core.messages import HumanMessage, SystemMessage

from app.config import chat_model
from app.services import vector_store
from app.models.schemas import PolicyQueryRequest, PolicyQueryResponse

router = APIRouter(tags=["Policy"])


@router.post("/ai/upload-policy")
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


@router.post("/ai/clear-policy")
def clear_policy_endpoint():
    """Clears all stored corporate policy vectors from the database."""
    try:
        vector_store.clear_policy_documents()
        return {"message": "Policy store cleared successfully."}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error clearing policy store: {str(e)}")



@router.post("/ai/query-policy", response_model=PolicyQueryResponse)
def query_policy_endpoint(request: PolicyQueryRequest):
    """Answers a question about the uploaded policy document using RAG."""
    if not chat_model:
        raise HTTPException(status_code=500, detail="LLM model not initialized")
        
    try:
        # Search vector store for relevant policies
        policy_contexts = vector_store.query_policy(request.question, k=4)
    except Exception as e:
        policy_contexts = []
        print(f"Error querying policy vector store: {e}")
        
    context_text = "\n---\n".join(policy_contexts) if policy_contexts else "No relevant policy details found."
    
    system_prompt = (
        "You are an AI Assistant specializing in corporate policies.\n"
        "Answer the user's question accurately using ONLY the provided policy context details below.\n"
        "If the answer is not explicitly covered in the context, politely state that the policy does not cover this information."
    )
    
    user_prompt = f"""
Retrieved Policy Excerpts:
{context_text}

User Question: {request.question}
"""

    try:
        messages = [
            SystemMessage(content=system_prompt),
            HumanMessage(content=user_prompt)
        ]
        response = chat_model.invoke(messages)
        return PolicyQueryResponse(answer=str(response.content))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"LLM QA failed: {str(e)}")
