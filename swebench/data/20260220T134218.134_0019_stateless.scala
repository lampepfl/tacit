requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14411/repo") { 
  val f = access("django/contrib/auth/forms.py")
  val lines = f.readLines()
  val newLines = lines.zipWithIndex.map((line, idx) => {
    if idx == 49 then
      "        context['summary'] = summary\n        return context\n\n    def id_for_label(self, id_):\n        return None"
    else if idx == 50 then
      ""
    else
      line
  }).mkString("\n")
  access("django/contrib/auth/forms.py").write(newLines)
}
