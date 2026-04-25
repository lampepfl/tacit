requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13757/repo") {
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13757/repo/django/db/models/fields/json.py")
  val content = file.read()
  val lines = content.split("\n").toList
  
  // Line 371 (index 370) should become:
  //         return HasKey(self.lhs.lhs, self.lhs.key_name).as_oracle(
  //             compiler, connection, template="NOT JSON_EXISTS(%s, '%s')"
  //         )
  // Line 376 (index 375) should become:
  //         return HasKey(self.lhs.lhs, self.lhs.key_name).as_sqlite(
  //             compiler, connection, template='JSON_TYPE(%s, %s) IS NULL'
  //         )
  
  val newLines = lines.updated(370, "        return HasKey(self.lhs.lhs, self.lhs.key_name).as_oracle(").
    updated(371, "            compiler, connection, template=\"NOT JSON_EXISTS(%s, '%s')\"").
    updated(372, "        )").
    updated(374, "        return HasKey(self.lhs.lhs, self.lhs.key_name).as_sqlite(").
    updated(375, "            compiler, connection, template='JSON_TYPE(%s, %s) IS NULL'").
    updated(376, "        )")
  
  val newContent = newLines.mkString("\n")
  file.write(newContent)
  println("File updated successfully")
}