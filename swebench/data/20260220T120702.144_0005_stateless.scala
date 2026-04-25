requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  val f = access("django/utils/http.py")
  val content = f.read()
  
  val oldCode = """    try:
        year = int(m.group('year'))
        if year < 100:
            if year < 70:
                year += 2000
            else:
                year += 1900
        month = MONTHS.index(m.group('mon').lower()) + 1"""
  
  val newCode = """    try:
        year = int(m.group('year'))
        if year < 100:
            current_year = datetime.datetime.utcnow().year
            year += (current_year // 100) * 100
            if year > current_year + 50:
                year -= 100
        month = MONTHS.index(m.group('mon').lower()) + 1"""
  
  val newContent = content.replace(oldCode, newCode)
  f.write(newContent)
  println("File updated successfully")
}