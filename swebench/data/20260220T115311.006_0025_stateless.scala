requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11039/repo") {
  val sqlmigrate = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11039/repo/django/core/management/commands/sqlmigrate.py")
  val content = sqlmigrate.read()
  val newContent = content.replace(
    "        # Show begin/end around output only for atomic migrations\n        self.output_transaction = migration.atomic",
    "        # Show begin/end around output only for atomic migrations on databases that support transactional DDL.\n        self.output_transaction = migration.atomic and connection.features.can_rollback_ddl"
  )
  sqlmigrate.write(newContent)
  println("Done")
}