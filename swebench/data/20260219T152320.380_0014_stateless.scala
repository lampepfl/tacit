requestExecPermission(Set("python", "python3")) {
  exec("python", List("-c", "
from astropy.modeling import models as m
from astropy.modeling.separable import separability_matrix
import numpy as np

cm = m.Linear1D(10) & m.Linear1D(5)
print('Linear1D & Linear1D:')
print(separability_matrix(cm))

print()
print('Pix2Sky_TAN & (Linear1D & Linear1D):')
result = separability_matrix(m.Pix2Sky_TAN() & cm)
print(result)

print()
print('Expected:')
expected = np.array([[ True,  True, False, False],
                    [ True,  True, False, False],
                    [False, False,  True, False],
                    [False, False, False,  True]])
print(expected)

print()
print('Test passed:', np.array_equal(result, expected))
"))
}