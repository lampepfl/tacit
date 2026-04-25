requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo") { 
  requestExecPermission(Set("python")) {
    exec("python", List("-c", """
import sys
# Try importing just the fixedwidth module which rst depends on
sys.path.insert(0, '.')

# First check if we can at least compile the file
import py_compile
py_compile.compile('astropy/io/ascii/rst.py', doraise=True)
print('Syntax check passed!')

# Now try importing the modules that don't need C extensions
from astropy.io.ascii import fixedwidth
print('FixedWidth import OK')

# Check that the RST class has the header_rows parameter
import inspect
sig = inspect.signature(fixedwidth.RST.__init__)
params = list(sig.parameters.keys())
print(f'RST.__init__ params: {params}')
if 'header_rows' in params:
    print('SUCCESS: header_rows parameter is accepted!')
else:
    print('FAILURE: header_rows parameter not found')
"""), Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo"))
  }
}
