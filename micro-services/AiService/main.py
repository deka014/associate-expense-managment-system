from fastapi import FastAPI
from app.routes import chat, policy, validation, audit

app = FastAPI(title="AI Agent Service", version="1.0.0")

# Register all route modules
app.include_router(chat.router)
app.include_router(policy.router)
app.include_router(validation.router)
app.include_router(audit.router)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
