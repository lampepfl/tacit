requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  val f = access("django/utils/http.py")
  val content = f.read()
  
  // Show exact bytes character by character for lines 176-181
  val lines = content.split("\n")
  for i <- 176 to 181 do
    val line = lines(i)
    println(s"Line $i: [${line}]")
    println(s"  Length: ${line.length}")
    // Show each char
    for j <- 0 until Math.min(line.length, 20) do
      print(f" '${line(j)}'=${line(j).toInt}")
    println()
}