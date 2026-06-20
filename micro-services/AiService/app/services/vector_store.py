import os
import io
import pypdf
from langchain_community.vectorstores import Chroma
from langchain_community.embeddings import HuggingFaceEmbeddings
from langchain_text_splitters import RecursiveCharacterTextSplitter

# Directory for persistent ChromaDB storage
DB_PATH = os.path.join(os.path.dirname(__file__), "..", "..", "chroma_db")

# Use a lightweight, free local embedding model (runs completely offline)
embeddings = HuggingFaceEmbeddings(model_name="all-MiniLM-L6-v2")

# Initialize/load the vector store
vector_store = Chroma(
    collection_name="policy_documents",
    embedding_function=embeddings,
    persist_directory=DB_PATH
)

def clear_policy_documents():
    """Deletes and recreates the ChromaDB collection to ensure outdated policies do not conflict."""
    global vector_store
    try:
        vector_store.delete_collection()
    except Exception:
        pass
    
    vector_store = Chroma(
        collection_name="policy_documents",
        embedding_function=embeddings,
        persist_directory=DB_PATH
    )

def add_policy_document(pdf_bytes: bytes, filename: str):
    """Parses a PDF file in memory, chunks the text, and stores embeddings in ChromaDB."""
    # Read PDF bytes
    pdf_file = io.BytesIO(pdf_bytes)
    reader = pypdf.PdfReader(pdf_file)
    text = ""
    for page in reader.pages:
        page_text = page.extract_text()
        if page_text:
            text += page_text + "\n"
            
    if not text.strip():
        raise ValueError("No extractable text found in the PDF document.")
    
    # Split text into chunks
    text_splitter = RecursiveCharacterTextSplitter(chunk_size=800, chunk_overlap=150)
    chunks = text_splitter.split_text(text)
    
    # Generate metadata for chunk tracking
    metadatas = [{"source": filename, "chunk": i} for i in range(len(chunks))]
    
    # Clear any previous policies to avoid conflicts, then insert new embeddings
    clear_policy_documents()
    vector_store.add_texts(texts=chunks, metadatas=metadatas)

def query_policy(question: str, k: int = 3):
    """Performs a similarity search in ChromaDB matching the user's question or expense info."""
    results = vector_store.similarity_search(question, k=k)
    return [doc.page_content for doc in results]
