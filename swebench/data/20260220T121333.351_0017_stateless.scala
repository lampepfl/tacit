requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11815/repo") {
  val serializer = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11815/repo/django/db/migrations/serializer.py")
  val lines = serializer.readLines().toList
  
  // Fix the indentation - class should have 4 spaces, method should have 8 spaces
  val newEnumSerializer = """class EnumSerializer(BaseSerializer):
    def serialize(self):
        enum_class = self.value.__class__
        module = enum_class.__module__
        return "%s.%s[%r]" % (module, enum_class.__name__, self.value.name), {'import %s' % module}"""
  
  val newLines = lines.patch(117, List(newEnumSerializer), 5)
  serializer.write(newLines.mkString("\n"))
  println("File updated")
}