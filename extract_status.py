import re
with open('/tmp/session2_v3.html') as f:
    html = f.read()

# Find all td with status-related patterns
pattern = r'<td style="text-align: center; vertical-align: middle; border: solid black 1px; background-color: #[0-9A-Fa-f]+; ">([^<]+)</td>'
matches = re.findall(pattern, html)
print(f"Found {len(matches)} status cells")
for i, m in enumerate(matches[:15]):
    print(f'{i}: "{m}"')
