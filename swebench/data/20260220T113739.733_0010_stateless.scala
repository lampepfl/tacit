requestExecPermission(Set("python")) { 
  val result = exec("python", List("-c", """
from astropy.modeling import models as m
from astropy.modeling.separable import separability_matrix
import numpy as np

cm = m.Linear1D(10) & m.Linear1D(5)

print("Test 1: cm (Linear1D & Linear1D)")
result1 = separability_matrix(cm)
print(result1)
print("Expected: diagonal matrix")

print()
print("Test 2: Pix2Sky_TAN() & Linear1D(10) & Linear1D(5)")
result2 = separability_matrix(m.Pix2Sky_TAN() & m.Linear1D(10) & m.Linear1D(5))
print(result2)

print()
print("Test 3: Pix2Sky_TAN() & cm (nested)")
result3 = separability_matrix(m.Pix2Sky_TAN() & cm)
print(result3)
print("Expected: same as Test 2")

# Verify correctness
expected = np.array([[ True,  True, False, False],
                     [ True,  True, False, False],
                     [False, False,  True, False],
                     [False, False, False,  True]])

if np.array_equal(result3, expected):
    print("SUCCESS: Bug is fixed!")
else:
    print("FAILURE: Bug NOT fixed")
"""), None, 60000)
  println(result.stdout)
  println(result.stderr)
}