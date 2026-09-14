import os
from dotenv import load_dotenv
from supabase import create_client

load_dotenv()

url = os.getenv("SUPABASE_URL")
key = os.getenv("SUPABASE_SECRET_KEY")

if not url or not key:
    raise ValueError("Supabase credentials not found in .env")

supabase = create_client(url, key)

bucket = "evidence-images"
local_path = "test_capture.png"

storage_path = (
    "TEST_OFFICER_001/OPI-001/test_capture.png"
)

with open(local_path, "rb") as file:
    image_bytes = file.read()

supabase.storage.from_(bucket).upload(
    storage_path,
    image_bytes,
    {
        "content-type": "image/png"
    }
)

print("Upload successful!")
print(f"Storage path: {storage_path}")

