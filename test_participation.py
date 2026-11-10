import re
html = open('C:/Users/mario/AppData/Local/Temp/session2_v3.html').read()

# Find first participation row
m = re.search(r'<tr id="participation-20"[^>]*>.*?</tr>', html, re.DOTALL)
if m:
    row = m.group(0)
    # Find all td elements
    tds = re.findall(r'<td[^>]*>(.*?)</td>', row, re.DOTALL)
    print(f'Found {len(tds)} columns:')
    for i, td in enumerate(tds):
        # Remove HTML tags
        content = re.sub(r'<[^>]+>', '', td).strip()
        print(f'  Col {i}: {content[:100]}')
else:
    print('No row found')
