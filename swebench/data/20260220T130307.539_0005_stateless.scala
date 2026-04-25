
import language.experimental.captureChecking

requestFileSystem(".")(fs =>
  find(".", "*").mkString("\n")
)
