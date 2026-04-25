requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  requestExecPermission(Set("python")) {
    val result = exec("python", List("-c", """
import numpy as np
from astropy.nddata import NDDataRef

array = np.array([[0, 1, 0], [1, 0, 1], [0, 1, 0]])
mask = np.array([[0, 1, 64], [8, 0, 1], [2, 1, 0]])

nref_nomask = NDDataRef(array)
nref_mask = NDDataRef(array, mask=mask)

# multiply no mask by constant (no mask * no mask)
result1 = nref_nomask.multiply(1., handle_mask=np.bitwise_or).mask
print(f"Test 1 (no mask * no mask): {result1}")

# multiply no mask by itself (no mask * no mask)
result2 = nref_nomask.multiply(nref_nomask, handle_mask=np.bitwise_or).mask
print(f"Test 2 (no mask * no mask): {result2}")

# multiply mask by constant (mask * no mask)
result3 = nref_mask.multiply(1., handle_mask=np.bitwise_or).mask
print(f"Test 3 (mask * no mask): {result3}")

# multiply mask by itself (mask * mask)
result4 = nref_mask.multiply(nref_mask, handle_mask=np.bitwise_or).mask
print(f"Test 4 (mask * mask):")
print(result4)

# multiply mask by no mask (mask * no mask)
result5 = nref_mask.multiply(nref_nomask, handle_mask=np.bitwise_or).mask
print(f"Test 5 (mask * no mask): {result5}")

print("All tests passed!")
"""), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo"), timeoutMs = 60000)
    println(s"Exit code: ${result.exitCode}")
    println(s"Stdout: ${result.stdout}")
    println(s"Stderr: ${result.stderr}")
  }
}