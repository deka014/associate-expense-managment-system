from pydantic import BaseModel
from typing import List, Optional


# --- Chat ---
class ChatRequest(BaseModel):
    message: str

class ChatResponse(BaseModel):
    response: str


# --- Policy Query ---
class PolicyQueryRequest(BaseModel):
    question: str

class PolicyQueryResponse(BaseModel):
    answer: str


# --- Expense Validation ---
class ExpenseValidationRequest(BaseModel):
    amount: float
    category: str
    description: str

class ExpenseValidationResponse(BaseModel):
    compliant: bool
    reason: str


# --- Audit / Fraud Detection ---
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
