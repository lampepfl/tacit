
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13448/repo") {
  val creationFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13448/repo/django/db/backends/base/creation.py")
  val content = creationFile.read()
  
  // Fix: change line 77 from "if serialize:" to "if serialize and self.connection.settings_dict['TEST']['MIGRATE']:"
  val fixedContent = content.replace(
    "        if serialize:\n            self.connection._test_serialized_contents = self.serialize_db_to_string()",
    "        if serialize and self.connection.settings_dict['TEST']['MIGRATE']:\n            self.connection._test_serialized_contents = self.serialize_db_to_string()"
  )
  
  creationFile.write(fixedContent)
  println("File updated successfully")
}
