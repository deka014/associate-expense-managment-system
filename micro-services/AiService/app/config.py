import os
from langchain_openai import ChatOpenAI
from dotenv import load_dotenv

# Load environment variables from the .env file one level up (in the micro-services directory)
load_dotenv(dotenv_path=os.path.join(os.path.dirname(__file__), "..", "..", ".env"))

# Setup OpenRouter Client
# Ensure you set the OPENROUTER_API_KEY environment variable in docker-compose.yml or your .env file
OPENROUTER_API_KEY = os.getenv("OPENROUTER_API_KEY", "your-api-key-here")

chat_model = None

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
