
import scala.util.matching.Regex

requestFileSystem(".") {
  val emailRegex = """[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}""".r

  val filesToSearch = List(
    "projects/webapp/docs/changelog.md",
    "projects/webapp/docs/README.md",
    "projects/webapp/src/UsersController.scala",
    "projects/webapp/src/Main.scala"
  )

  for (path <- filesToSearch) {
    val file = access(path)
    val lines = file.readLines()
    for ((line, lineNum) <- lines.zipWithIndex) {
      val matches = emailRegex.findAllIn(line).toList
      if (matches.nonEmpty) {
        println(s"[$path:${lineNum + 1}] ${matches.mkString(", ")}")
      }
    }
  }
}
