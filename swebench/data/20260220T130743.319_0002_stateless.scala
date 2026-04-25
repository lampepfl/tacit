
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12700/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  // Read the file, find the cleanse_setting method, and fix it
  val filePath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12700/repo/django/views/debug.py"
  val content = access(filePath).read()
  
  // Find and replace the cleanse_setting method
  val oldCode = """    def cleanse_setting(self, key, value):
        \"\"\"
        Cleanse an individual setting key/value of sensitive content. If the
        value is a dictionary, recursively cleanse the keys in that dictionary.
        \"\"\"
        try:
            if self.hidden_settings.search(key):
                cleansed = self.cleansed_substitute
            elif isinstance(value, dict):
                cleansed = {k: self.cleanse_setting(k, v) for k, v in value.items()}
            else:
                cleansed = value
        except TypeError:
            # If the key isn't regex-able, just return as-is.
            cleansed = value

        if callable(cleansed):
            cleansed = CallableSettingWrapper(cleansed)

        return cleansed"""
  
  val newCode = """    def cleanse_setting(self, key, value):
        \"\"\"
        Cleanse an individual setting key/value of sensitive content. If the
        value is a dictionary, recursively cleanse the keys in that dictionary.
        \"\"\"
        try:
            if self.hidden_settings.search(key):
                cleansed = self.cleansed_substitute
            elif isinstance(value, dict):
                cleansed = {k: self.cleanse_setting(k, v) for k, v in value.items()}
            elif isinstance(value, (list, tuple)):
                cleansed = [self.cleanse_setting(key, v) for v in value]
            else:
                cleansed = value
        except TypeError:
            # If the key isn't regex-able, just return as-is.
            cleansed = value

        if callable(cleansed):
            cleansed = CallableSettingWrapper(cleansed)

        return cleansed"""
  
  val newContent = content.replace(oldCode, newCode)
  
  // Write the file back
  access(filePath).write(newContent)
  
  println("File updated successfully")
}
