import java.io.File

val repoPath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15061/repo"
val f = new File(repoPath)
println(f.listFiles().map(_.getName).mkString(", "))