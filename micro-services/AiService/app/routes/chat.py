from fastapi import APIRouter, HTTPException
from langchain_core.messages import HumanMessage, SystemMessage

from app.config import chat_model
from app.models.schemas import ChatRequest, ChatResponse

router = APIRouter(tags=["Chat"])


@router.get("/ai/health")
def health_check():
    return {"status": "up"}


@router.post("/ai/chat", response_model=ChatResponse)
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
