requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11815/repo") {
  val serializer = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11815/repo/django/db/migrations/serializer.py")
  val content = serializer.read()
  
  val oldCode = """class EnumSerializer(BaseSerializer):
    def serialize(self):
        enum_class = self.value.__class__
        module = enum_class.__module__
        v_string, v_imports = serializer_factory(self.value.value).serialize()
        imports = {'import %s' % module, *v_imports}
        return "%s.%s(%s)" % (module, enum_class.__name__, v_string), imports"""

  val newCode = """class EnumSerializer(BaseSerializer):
    def serialize(self):
        enum_class = self.value.__class__
        module = enum_class.__module__
        return "%s.%s[%r]" % (module, enum_class.__name__, self.value.name), {'import %s' % module}"""

  val newContent = content.replace(oldCode, newCode)
  serializer.write(newContent)
  println("File updated successfully")
}