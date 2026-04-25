requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  requestExecPermission(Set("python")) {
    val code = """
with open("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py") as f:
    content = f.read()

# Check for our fix
if 'elif self.mask is not None and operand.mask is None:' in content:
    print("SUCCESS: Fix is present in the file!")
    
    # Find and print the relevant lines
    lines = content.split('\\n')
    for i, line in enumerate(lines):
        if 'elif self.mask is not None and operand.mask is None:' in line:
            print(f"Line {i+1}: {line}")
            print(f"Line {i+2}: {lines[i+1]}")
            print(f"Line {i+3}: {lines[i+2]}")
else:
    print("ERROR: Fix not found in file!")
"""
    val result = exec("python", List("-c", code), timeoutMs = 60000)
    println(s"Exit code: ${result.exitCode}")
    println(s"Stdout: ${result.stdout}")
    if (result.stderr.nonEmpty) println(s"Stderr: ${result.stderr}")
  }
}