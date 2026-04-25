import scala.concurrent.duration._
import scala.util._
import java.nio.file._
import java.io._
import scala.jdk.CollectionConverters._

val p = java.nio.file.Files.list(Paths.get("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo"))
p.iterator().asScala.map(_.getFileName.toString).toList.take(20)