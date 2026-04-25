requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11815/repo") {
  val serializer = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11815/repo/django/db/migrations/serializer.py")
  val lines = serializer.readLines().toList
  
  // Fixed indentation - 4 spaces for class, 8 for method  
  val newEnumSerializer = """class EnumSerializer(BaseSerializer):
    def serialize(self):
        enum_class = self.value.__class__
        module = enum_class.__module__
        return "%s.%s[%r]" % (module, enum_class.__name__, self.value.name), {'import %s' % module}"""
  
  // Replace 6 lines (117-122, 0-indexed)
  val newLines = lines.patch(117, List(newEnumSerializer), 6)
  serializer.write(newLines.mkString("\n"))
  println("File updated")
}