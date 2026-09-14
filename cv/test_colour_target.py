import os
from dotenv import load_dotenv
from supabase import create_client

load_dotenv()

url = os.getenv("SUPABASE_URL")
key = os.getenv("SUPABASE_SECRET_KEY")

supabase = create_client(url, key)

test_id = "OPI-001"

response = (
    supabase
    .table("protocol_colour_targets")
    .select("*")
    .eq("test_id", test_id)
    .execute()
)

print("Database response:")
print(response.data)