requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  requestExecPermission(Set("python")) {
    // Try to run python with the module path directly without importing astropy
    val code = """
import sys
sys.path.insert(0, '/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo')

# Check if the file compiles without errors
import astropy.nddata.mixins.ndarithmetic as ndarithmetic
print("Module loaded successfully!")

# Read the method source to verify our fix is in place
import inspect
source = inspect.getsource(ndarithmetic.NDArithmeticMixin._arithmetic_mask)
if 'operand.mask is None' in source and 'self.mask is not None and operand.mask is None' in source:
    print("Fix verified in source!")
else:
    print("Warning: Fix may not be present")
print(source[:500])
"""
    val result = exec("python", List("-c", code), timeoutMs = 60000)
    println(s"Exit code: ${result.exitCode}")
    println(s"Stdout: ${result.stdout}")
    if (result.stderr.nonEmpty) println(s"Stderr: ${result.stderr}")
  }
}