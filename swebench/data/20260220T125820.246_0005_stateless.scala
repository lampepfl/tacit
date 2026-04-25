requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12983/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12983/repo/django/utils/text.py")
  val content = f.read()
  // Find the problematic lines and fix them
  val fixedContent = content.replace("value = re.sub(r'[^\\w\\s-]', '', value.lower()).strip()\nvalue = re.sub(r'[^\\w\\s-]', '', value.lower()).strip()", "value = re.sub(r'[^\\w\\s-]', '', value.lower()).strip()")
  f.write(fixedContent)
  println("Fixed!")
}