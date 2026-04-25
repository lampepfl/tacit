requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14608/repo") {
  val formsets = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14608/repo/django/forms/formsets.py")
  val content = formsets.read()
  
  // Make the two changes
  val modified = content
    .replace("self._non_form_errors = self.error_class()", "self._non_form_errors = self.error_class(error_class='nonform')")
    .replace("self._non_form_errors = self.error_class(e.error_list)", "self._non_form_errors = self.error_class(e.error_list, error_class='nonform')")
  
  formsets.write(modified)
  println("Done!")
}