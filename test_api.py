import urllib.request
import json

def call_api(url, method='GET'):
    req = urllib.request.Request(url, method=method)
    try:
        with urllib.request.urlopen(req) as response:
            print(f"URL: {url}")
            print(f"Status: {response.status}")
            print(f"Body: {response.read().decode()}")
    except Exception as e:
        print(f"Error calling {url}: {e}")

# Toggle to PROXY
call_api("http://localhost:8080/api/ratemypcbro/config/provider?type=PROXY", method='POST')

# Get General Verdict
call_api("http://localhost:8080/api/ratemypcbro")
