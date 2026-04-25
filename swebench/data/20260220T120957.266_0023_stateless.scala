requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  val f = access("django/utils/http.py")
  val content = f.read()
  
  // Get exact lines  
  val lines = content.split("\n")
  
  // Build exact wrong code
  val wrongCode = 
    lines(176) + "\n" + 
    lines(177) + "\n" + 
    lines(178) + "\n" + 
    lines(179) + "\n" + 
    lines(180) + "\n" + 
    lines(181) + "\n" + 
    lines(182)
  
  val correctCode = 
    "        year = int(m.group('year'))\n" +
    "        if year < 100:\n" +
    "            current_year = datetime.datetime.utcnow().year\n" +
    "            year += (current_year // 100) * 100\n" +
    "            if year > current_year + 50:\n" +
    "                year -= 100\n" +
    "        month = MONTHS.index(m.group('mon').lower()) + 1"
  
  if content.contains(wrongCode) then
    val newContent = content.replace(wrongCode, correctCode)
    f.write(newContent)
    println("Fixed!")
  else
    println("Not found!")
    println("Wrong code:")
    println(wrongCode.replace(" ", "·"))
}