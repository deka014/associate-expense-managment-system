from fastapi import APIRouter

from app.config import chat_model
from app.services import fraud_detector
from app.models.schemas import AuditRequest, AuditResponse, AuditResultItem

router = APIRouter(tags=["Audit"])


@router.post("/ai/audit", response_model=AuditResponse)
def audit_expenses(request: AuditRequest):
    """Audits a batch of expenses, cross-referencing each with its uploaded receipt PDF."""
    results = []
    for expense in request.expenses:
        audit_res = fraud_detector.audit_single_expense(expense.model_dump(), chat_model)
        results.append(AuditResultItem(**audit_res))
    return AuditResponse(audit_results=results)
