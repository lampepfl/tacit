requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15789/repo") {
  val htmlFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15789/repo/django/utils/html.py")
  val content = htmlFile.read()
  
  // Simple string replacements
  val step1 = content.replace("def json_script(value, element_id=None):", "def json_script(value, element_id=None, encoder=None):")
  val step2 = step1.replace("json.dumps(value, cls=DjangoJSONEncoder)", "json.dumps(value, cls=encoder or DjangoJSONEncoder)")
  
  htmlFile.write(step2)
  println("Done")
}