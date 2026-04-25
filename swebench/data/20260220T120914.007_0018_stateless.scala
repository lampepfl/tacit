requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  val f = access("django/utils/http.py")
  val content = f.read()
  
  // Use exact string from file
  val lines = content.split("\n")
  val oldCodeFromFile = lines(176) + "\n" + lines(177) + "\n" + lines(178) + "\n" + lines(179) + "\n" + lines(180) + "\n" + lines(181) + "\n" + lines(182)
  
  val newCode = """        year = int(m.group('year'))
        if year < 100:
            current_year = datetime.datetime.utcnow().year
            year += (current_year // 100) * 100
            if year > current_year + 50:
                year -= 100
        month = MONTHS.index(m.group('mon').lower()) + 1"""
  
  if content.contains(oldCodeFromFile) then
    val newContent = content.replace(oldCodeFromFile, newCode)
    f.write(newContent)
    println("File updated successfully")
  else
    println("Not found!")
    println(s"Content contains: ${content.contains(oldCodeFromFile)}")
}