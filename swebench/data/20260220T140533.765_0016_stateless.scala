requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15695/repo") {
  given fs: FileSystem = summon[FileSystem]
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15695/repo/django/db/migrations/operations/models.py")
  val content = file.read()
  
  // Find the exact text to replace
  val oldCode = """    def database_backwards(self, app_label, schema_editor, from_state, to_state):
        if self.old_fields:
            # Backward operation with unnamed index is a no-op.
            return

        self.new_name_lower, self.old_name_lower = ("""
  
  val newCode = """    def database_backwards(self, app_label, schema_editor, from_state, to_state):
        if self.old_fields:
            from_model = from_state.apps.get_model(app_label, self.model_name)
            columns = [
                from_model._meta.get_field(field).column for field in self.old_fields
            ]
            matching_index_name = schema_editor._constraint_names(
                from_model, column_names=columns, index=True
            )
            if len(matching_index_name) != 1:
                raise ValueError(
                    "Found wrong number (%s) of indexes for %s(%s)."
                    % (
                        len(matching_index_name),
                        from_model._meta.db_table,
                        ", ".join(columns),
                    )
                )
            old_index = models.Index(
                fields=self.old_fields,
                name=schema_editor._create_index_name(
                    from_model._meta.db_table, columns
                ),
            )
            new_index = models.Index(
                fields=self.old_fields,
                name=matching_index_name[0],
            )
            schema_editor.rename_index(from_model, old_index, new_index)
            return

        self.new_name_lower, self.old_name_lower = ("""
  
  val updatedContent = content.replace(oldCode, newCode)
  access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15695/repo/django/db/migrations/operations/models.py").write(updatedContent)
  println("File updated successfully")
}