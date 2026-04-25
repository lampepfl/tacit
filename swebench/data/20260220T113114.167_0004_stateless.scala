
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14365/repo") {
  given fs: FileSystem = summon
  access("test_fix.py").write("""
from astropy.table import Table

# Test with lowercase command
t = Table.read('test.qdp', format='ascii.qdp')
print("Table read successfully!")
print(t)
print("Column names:", t.colnames)
""")
}
