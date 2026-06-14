from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from langchain_openai import ChatOpenAI
from langchain_core.messages import HumanMessage, SystemMessage
import os
from dotenv import load_dotenv

# Load environment variables from the .env file one level up (in the micro-services directory)
load_dotenv(dotenv_path="../.env")

app = FastAPI(title="AI Agent Service", version="1.0.0")

# Setup OpenRouter Client
# Ensure you set the OPENROUTER_API_KEY environment variable in docker-compose.yml or your .env file
OPENROUTER_API_KEY = os.getenv("OPENROUTER_API_KEY", "your-api-key-here")

try:
    chat_model = ChatOpenAI(
        model="google/gemma-4-31b-it:free", # Let's use Google's Gemma 2 which is reliably free!
        openai_api_key=OPENROUTER_API_KEY,
        openai_api_base="https://openrouter.ai/api/v1",
        default_headers={
            "HTTP-Referer": "http://localhost:8000", # Required by OpenRouter
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
        # Fix: response.content is a string, which is compatible with response_model=ChatResponse
        return ChatResponse(response=str(response.content))
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
