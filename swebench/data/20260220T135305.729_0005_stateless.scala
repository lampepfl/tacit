import library.*
import language.experimental.captureChecking

@main def main: Unit = 
  requestFileSystem(".") { fs =>
    val entries = access(".").children
    entries.map(_.name).foreach(println)
  }