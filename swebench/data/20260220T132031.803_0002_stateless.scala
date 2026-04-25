requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13660/repo") {
  val shellFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13660/repo/django/core/management/commands/shell.py")
  val content = shellFile.read()
  
  // Fix the first exec call: exec(options['command']) -> exec(options['command'], {})
  val fixedContent = content.replace("exec(options['command'])", "exec(options['command'], {})")
                             .replace("exec(sys.stdin.read())", "exec(sys.stdin.read(), {})")
  
  shellFile.write(fixedContent)
  println("Fixed!")
}
