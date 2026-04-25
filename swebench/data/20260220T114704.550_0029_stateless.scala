requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo") { 
  requestExecPermission(Set("python")) {
    exec("python", List("-c", """
import re

# Read the file
with open('astropy/io/ascii/rst.py', 'r') as f:
    content = f.read()

# Replace the __init__ method
old_init = '''    def __init__(self):
        super().__init__(delimiter_pad=None, bookend=False)'''

new_init = '''    def __init__(self, header_rows=None):
        super().__init__(delimiter_pad=None, bookend=False, header_rows=header_rows)'''

content = content.replace(old_init, new_init)

# Replace the write method
old_write = '''    def write(self, lines):
        lines = super().write(lines)
        lines = [lines[1]] + lines + [lines[1]]
        return lines'''

new_write = '''    def write(self, lines):
        lines = super().write(lines)
        idx = len(self.data.header_rows)
        lines = [lines[idx]] + lines + [lines[idx]]
        return lines'''

content = content.replace(old_write, new_write)

# Write back
with open('astropy/io/ascii/rst.py', 'w') as f:
    f.write(content)

print('Done')
"""), Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo"))
  }
}
