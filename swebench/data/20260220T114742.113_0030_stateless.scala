requestExecPermission(Set("python", "python3")) {
  exec("python3", List("-c", """
import sys
sys.path.insert(0, '/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo')

from astropy.wcs import WCS
import numpy as np

# Create a simple 2D WCS
wcs = WCS(naxis=2)
wcs.wcs.crpix = [1, 1]
wcs.wcs.cdelt = [1, 1]
wcs.wcs.crval = [1, 1]
wcs.wcs.ctype = ['GLON-CAR', 'GLAT-CAR']

# Test with empty lists
result = wcs.wcs_pix2world([], [], 0)
print('Result with empty lists:', result)
print('Types:', type(result[0]), type(result[1]))

# Test with empty arrays
result2 = wcs.wcs_pix2world(np.array([]), np.array([]), 0)
print('Result with empty arrays:', result2)

# Test that normal use still works
result3 = wcs.wcs_pix2world([1], [1], 0)
print('Result with valid input:', result3)
"""), None, 60000)
}