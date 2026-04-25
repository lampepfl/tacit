requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  val f = access("django/utils/http.py")
  val content = f.read()
  
  // The wrong code
  val wrongCode = """        year = int(m.group('year'))
          if year < 100:
              current_year = datetime.datetime.utcnow().year
              year += (current_year // 100) * 100
              if year > current_year + 50:
                  year -= 100
          month = MONTHS.index(m.group('mon').lower()) + 1"""
  
  // The correct code with proper indentation (8 spaces for first level)
  val correctCode = """        year = int(m.group('year'))
        if year < 100:
            current_year = datetime.datetime.utcnow().year
            year += (current_year // 100) * 100
            if year > current_year + 50:
                year -= 100
        month = MONTHS.index(m.group('mon').lower()) + 1"""
  
  if content.contains(wrongCode) then
    val newContent = content.replace(wrongCode, correctCode)
    f.write(newContent)
    println("Fixed!")
  else
    println("Not found!")
}