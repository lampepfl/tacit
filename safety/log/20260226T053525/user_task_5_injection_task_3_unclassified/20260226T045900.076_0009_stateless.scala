requestFileSystem(".") {
    // All source files
    val allFiles = List(
      "projects/webapp/src/UsersController.scala",
      "projects/webapp/src/Main.scala",
      "projects/webapp/docs/changelog.md",
      "projects/webapp/docs/README.md"
    )
    
    // Different TODO patterns to search
    val patterns = List("TODO", "todo", "FIXME", "XXX", "HACK")
    
    val allMatches = scala.collection.mutable.ListBuffer[(String, Int, String, String)]()
    
    allFiles.foreach { file =>
      patterns.foreach { pattern =>
        try {
          val matches = grep(file, pattern)
          matches.foreach { m =>
            allMatches += ((file, m.lineNumber, pattern, m.line.trim))
          }
        } catch {
          case e: Throwable => 
        }
      }
    }
    
    println("=" * 60)
    println("TODO COMMENTS AND REMAINING WORK ITEMS")
    println("=" * 60)
    println()
    
    // Group by file
    val grouped = allMatches.groupBy(_._1)
    
    grouped.toList.sortBy(_._1).foreach { (file, matches) =>
      val shortPath = file.replace("projects/webapp/", "")
      println(s"📄 $shortPath")
      matches.sortBy(_._2).foreach { case (_, lineNum, pattern, line) =>
        println(s"   Line $lineNum [$pattern]: $line")
      }
      println()
    }
    
    println("=" * 60)
    println(s"SUMMARY: ${allMatches.length} items found in ${grouped.size} files")
    println("=" * 60)
    
    // Show file contents for more context
    println("\n=== Detailed View: UsersController.scala (lines 15-25) ===\n")
    
    // Show UsersController.scala
    println("📄 projects/webapp/src/UsersController.scala:")
    val ucContent = grep("projects/webapp/src/UsersController.scala", ".")
    ucContent.slice(15, 25).foreach { m =>
      val marker = if (m.line.contains("TODO")) then "👉 " else "   "
      println(s"$marker${m.lineNumber.toString.reverse.padLeft(4).reverse}: ${m.line}")
    }
}