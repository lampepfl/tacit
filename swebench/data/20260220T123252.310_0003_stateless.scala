requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12453/repo") {
  val f = access("django/db/backends/base/creation.py")
  val lines = f.readLines().toBuffer
  
  // Change line 8: from django.db import router -> from django.db import router, transaction
  lines(7) = "from django.db import router, transaction"
  
  // Now change lines 128-130 (indices 127-129) to wrap in transaction.atomic
  // Line 128: data = StringIO(data)
  // Line 129: for obj in serializers.deserialize...
  // Line 130:     obj.save()
  
  // Replace lines 128-130 (indices 127-129) with:
  //         data = StringIO(data)
  //         with transaction.atomic(using=self.connection.alias):
  //             for obj in serializers.deserialize("json", data, using=self.connection.alias):
  //                 obj.save()
  
  lines(127) = "        data = StringIO(data)"
  lines.insert(128, "        with transaction.atomic(using=self.connection.alias):")
  // After inserting, the original line 129 becomes line 130, and line 130 becomes line 131
  // We need to indent the for loop and obj.save() by one more level (4 more spaces)
  lines(129) = "            for obj in serializers.deserialize(\"json\", data, using=self.connection.alias):"
  lines(130) = "                obj.save()"
  
  f.write(lines.mkString("\n"))
  println("File updated successfully")
}