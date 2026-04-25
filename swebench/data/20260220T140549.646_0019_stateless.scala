requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15695/repo") {
  given fs: FileSystem = summon[FileSystem]
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15695/repo/django/db/migrations/operations/models.py")
  val content = file.read()
  val idx = content.indexOf("class RenameIndex")
  val classContent = content.substring(idx, idx + 10000)
  val dbBackwardsIdx = classContent.indexOf("def database_backwards")
  println(classContent.substring(dbBackwardsIdx, dbBackwardsIdx + 800))
}