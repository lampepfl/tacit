requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo") { 
  requestExecPermission(Set("python")) {
    exec("python", List("-c", """
import sys
sys.path.insert(0, '/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo')

# Import the RST module directly
from astropy.io.ascii import rst

# Check if header_rows parameter is accepted
print('Testing RST class with header_rows parameter...')
try:
    writer = rst.RST(header_rows=['name', 'unit'])
    print('SUCCESS: RST accepts header_rows parameter')
except TypeError as e:
    print(f'FAILURE: {e}')
"""), Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo"))
  }
}
