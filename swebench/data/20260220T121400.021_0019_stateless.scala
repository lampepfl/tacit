requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11815/repo") {
  val serializer = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11815/repo/django/db/migrations/serializer.py")
  val lines = serializer.readLines().toList
  
  // Fixed indentation - need exactly 4 spaces for class, 8 for method
  val newEnumSerializer = """class EnumSerializer(BaseSerializer):
    def serialize(self):
        enum_class = self.value.__class__
        module = enum_class.__module__
        return "%s.%s[%r]" % (module, enum_class.__name__, self.value.name), {'import %s' % module}"""
  
  // Let me check how many lines to replace
  println(s"Line 118: '${lines(117)}'")
  println(s"Line 119: '${lines(118)}'")
  println(s"Line 120: '${lines(119)}'")
  println(s"Line 121: '${lines(120)}'")
  println(s"Line 122: '${lines(121)}'")
  println(s"Line 123: '${lines(122)}'")
}