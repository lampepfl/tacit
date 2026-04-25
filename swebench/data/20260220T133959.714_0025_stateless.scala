requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13757/repo") {
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13757/repo/django/db/models/fields/json.py")
  val content = file.read()
  val lines = content.split("\n").toList
  
  // The changes we need:
  // Line 370 (index 369): "        return super().as_sql(compiler, connection)" 
  //   -> "        return HasKey(self.lhs.lhs, self.lhs.key_name).as_oracle("
  //                                           + "\n            compiler, connection, template=\"NOT JSON_EXISTS(%s, '%s')\""
  //                                           + "\n        )"
  // Line 375 (index 374): "        return super().as_sql(compiler, connection)"
  //   -> "        return HasKey(self.lhs.lhs, self.lhs.key_name).as_sqlite("
  //                                           + "\n            compiler, connection, template='JSON_TYPE(%s, %s) IS NULL'"
  //                                           + "\n        )"
  
  val newLines = lines.updated(370, "        return HasKey(self.lhs.lhs, self.lhs.key_name).as_oracle(").
    updated(371, "            compiler, connection, template=\"NOT JSON_EXISTS(%s, '%s')\"").
    updated(372, "        )").
    updated(375, "        return HasKey(self.lhs.lhs, self.lhs.key_name).as_sqlite(").
    updated(376, "            compiler, connection, template='JSON_TYPE(%s, %s) IS NULL'").
    updated(377, "        )")
  
  val newContent = newLines.mkString("\n")
  file.write(newContent)
  println("Done")
}