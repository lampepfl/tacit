requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  requestExecPermission(Set("python")) {
    // Import directly without going through astropy package
    val code = """
import sys
import importlib.util

# Load the module directly from file
spec = importlib.util.spec_from_file_location("ndarithmetic", "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
ndarithmetic = importlib.util.module_from_spec(spec)

# Check the source code directly
with open("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py") as f:
    content = f.read()
    
# Find the _arithmetic_mask method
import re
match = re.search(r'def _arithmetic_mask\(.*?\n(?:.*?\n)*?(?=\n    def |\Z)', content)
if match:
    print("Found _arithmetic_mask method:")
    print(match.group(0)[:800])
else:
    print("Method not found")
"""
    val result = exec("python", List("-c", code), timeoutMs = 60000)
    println(s"Exit code: ${result.exitCode}")
    println(s"Stdout: ${result.stdout}")
    if (result.stderr.nonEmpty) println(s"Stderr: ${result.stderr}")
  }
}