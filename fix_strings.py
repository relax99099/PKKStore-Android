import os
import re
import xml.etree.ElementTree as ET

def fix_file(path):
    print(f'Fixing {path}')
    try:
        with open(path, 'r', encoding='utf-8') as f:
            content = f.read()

        # 1. Fix unescaped ampersands
        content = re.sub(r'&(?!(amp|lt|gt|quot|apos);)', '&amp;', content)

        # 2. Fix invalid backslash escapes
        def fix_backslash(match):
            s = match.group(0)
            if re.match(r'\\(?![nt"\'\\u])', s):
                return s
            return '\\\\' + s[1:]
        content = re.sub(r'\\.', fix_backslash, content)

        # 3. Fix non-positional placeholders
        def fix_placeholders(match):
            tag_attr = match.group(1)
            tag_content = match.group(2)
            if 'translatable="false"' in tag_attr:
                return match.group(0)
            
            pattern = r'(?<!%)(%[a-zA-Z])'
            placeholders = re.findall(pattern, tag_content)
            if len(placeholders) > 1:
                if not re.search(r'%\d+\$', tag_content):
                    new_content = tag_content
                    for i, p in enumerate(placeholders):
                        new_content = new_content.replace(p, f'%{i+1}${p[1]}', 1)
                    return f'<string{tag_attr}>{new_content}</string>'
            return match.group(0)

        content = re.sub(r'<string([^>]*)>(.*?)</string>', fix_placeholders, content, flags=re.DOTALL)

        # 4. Remove duplicate string entries
        try:
            root = ET.fromstring(content)
            seen = set()
            to_remove = []
            for child in root.findall('string'):
                name = child.get('name')
                if name in seen:
                    to_remove.append(child)
                else:
                    seen.add(name)
            for child in to_remove:
                root.remove(child)
            content = ET.tostring(root, encoding='unicode')
            if not content.startswith('<?xml'):
                content = '<?xml version="1.0" encoding="utf-8"?>\n' + content
        except Exception as e:
            print(f'Error parsing XML in {path}: {e}')

        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)
    except Exception as e:
        print(f'Failed to fix {path}: {e}')

for root, dirs, files in os.walk('app/src/main/res'):
    for file in files:
        if file == 'strings.xml':
            fix_file(os.path.join(root, file))
