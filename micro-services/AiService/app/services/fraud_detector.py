import base64
import io
import pypdf
import json
import re
from langchain_core.messages import HumanMessage, SystemMessage


def extract_text_from_pdf_bytes(pdf_bytes: bytes) -> str:
    """Extracts raw text content from PDF bytes in memory, falling back to OCR if digital text is empty."""
    text = ""
    try:
        pdf_file = io.BytesIO(pdf_bytes)
        reader = pypdf.PdfReader(pdf_file)
        for page in reader.pages:
            page_text = page.extract_text()
            if page_text:
                text += page_text + "\n"
    except Exception as e:
        print(f"Error during digital PDF text extraction: {e}")

    # Fallback to OCR if no digital text was found (e.g. scanned PDF / receipt image embedded in PDF)
    if not text.strip():
        print("Digital extraction returned empty text. Falling back to EasyOCR...")
        try:
            import fitz  # PyMuPDF
            import easyocr
            import numpy as np
            from PIL import Image
            
            # Initialize EasyOCR reader (downloads ~100MB model on first run, then cached)
            reader = easyocr.Reader(['en'], gpu=False)
            
            # Open PDF from bytes natively using PyMuPDF
            doc = fitz.open(stream=pdf_bytes, filetype="pdf")
            ocr_text = ""
            for i in range(len(doc)):
                page = doc[i]
                pix = page.get_pixmap()
                img_data = pix.tobytes("png")
                image = Image.open(io.BytesIO(img_data))
                
                # EasyOCR expects a numpy array
                img_array = np.array(image)
                results = reader.readtext(img_array, detail=0)
                page_text = " ".join(results)
                if page_text.strip():
                    ocr_text += f"\n--- Page {i+1} OCR ---\n" + page_text
            text = ocr_text.strip()
        except Exception as ocr_err:
            print(f"Error during OCR fallback processing: {ocr_err}")
            
    return text.strip()


def audit_single_expense(expense: dict, chat_model) -> dict:
    """
    Audits a single expense item by decoding its receipt PDF, extracting text, 
    and prompting the LLM to verify matching details.
    """
    expense_id = expense.get("id")
    amount = expense.get("amount")
    category = expense.get("category")
    date = expense.get("date", "N/A")
    description = expense.get("description", "N/A")
    receipt_base64 = expense.get("receipt_base64")
    
    # Check for missing receipt
    if not receipt_base64:
        return {
            "expense_id": expense_id,
            "flags": ["MISSING_RECEIPT: No receipt uploaded for this expense"],
            "reason": "Receipt is missing.",
            "status": "FLAGGED"
        }
        
    # Decode receipt PDF
    try:
        pdf_bytes = base64.b64decode(receipt_base64)
    except Exception:
        return {
            "expense_id": expense_id,
            "flags": ["INVALID_RECEIPT: Receipt file is not a valid base64 string"],
            "reason": "Failed to decode base64 receipt data.",
            "status": "FLAGGED"
        }
        
    # Extract text from PDF
    receipt_text = extract_text_from_pdf_bytes(pdf_bytes)
    if not receipt_text:
        return {
            "expense_id": expense_id,
            "flags": ["UNREADABLE_RECEIPT: Could not extract text from the uploaded PDF receipt"],
            "reason": "Receipt PDF is empty or unreadable.",
            "status": "FLAGGED"
        }
        
    # Verify AI model exists
    if not chat_model:
        return {
            "expense_id": expense_id,
            "flags": ["SYSTEM_ERROR: AI model not initialized for audit"],
            "reason": "LLM agent is unavailable.",
            "status": "ERROR"
        }
        
    system_prompt = (
        "You are an AI Expense Auditor. You cross-check logged expense details against the text extracted from the employee's receipt.\n"
        "Analyze the details for mismatches in amount, category, date, or merchant.\n"
        "Respond ONLY with a JSON object in this format:\n"
        "{\n"
        "  \"flags\": [\"FLAG_NAME: details\"],\n"
        "  \"reason\": \"explanation\"\n"
        "}\n"
        "If everything matches perfectly, return an empty list [] for flags and a success message for reason."
    )
    
    user_prompt = f"""
Logged Expense Details:
- ID: {expense_id}
- Amount: {amount}
- Category: {category}
- Date: {date}
- Description: {description}

Extracted Receipt Text:
{receipt_text}
"""

    try:
        messages = [
            SystemMessage(content=system_prompt),
            HumanMessage(content=user_prompt)
        ]
        response = chat_model.invoke(messages)
        res_text = str(response.content)
        
        # Parse JSON from response
        parsed = {"flags": [], "reason": "No mismatches detected."}
        match = re.search(r"\{.*\}", res_text, re.DOTALL)
        if match:
            try:
                parsed = json.loads(match.group(0))
            except Exception:
                pass
        else:
            try:
                parsed = json.loads(res_text)
            except Exception:
                pass
                
        flags = parsed.get("flags", [])
        reason = parsed.get("reason", "Verification complete.")
        status = "FLAGGED" if flags else "CLEARED"
        
        return {
            "expense_id": expense_id,
            "flags": flags,
            "reason": reason,
            "status": status
        }
    except Exception as e:
        return {
            "expense_id": expense_id,
            "flags": [f"SYSTEM_ERROR: Audit failed due to exception: {str(e)}"],
            "reason": "An error occurred during LLM processing.",
            "status": "ERROR"
        }
