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
image_path = "TEST_OFFICER_001/BAR-001/evidence_1789291975802.jpg"

image_bytes = supabase.storage.from_(bucket).download(image_path)

output_path = "downloaded_test.jpg"

with open(output_path, "wb") as f:
    f.write(image_bytes)

print(f"Downloaded successfully: {output_path}")
print(f"File size: {len(image_bytes)} bytes")