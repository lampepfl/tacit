
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12497/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12497/repo/django/db/models/fields/related.py")
  val content = f.read()
  val countForeignKey = content.sliding(1).count(_.mkString == "F")
  // Count occurrences of the exact string
  val occurrences = content.split("ForeignKey").length - 1
  println(s"Occurrences of 'ForeignKey': $occurrences")
}
