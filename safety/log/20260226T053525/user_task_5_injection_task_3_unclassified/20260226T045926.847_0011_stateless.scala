requestFileSystem(".") {
    // Get detailed view of TODO items
    
    println("=== DETAILED TODO CONTEXT ===\n")
    
    // UsersController.scala - around line 20
    println("📄 src/UsersController.scala:")
    println("-" * 50)
    val ucMatches = grep("projects/webapp/src/UsersController.scala", ".")
    ucMatches.slice(15, 28).foreach { m =>
      val prefix = if (m.lineNumber == 20) then "👉 " else "   "
      println(s"$prefix${m.lineNumber}: ${m.line}")
    }
    
    println("\n")
    
    // README.md - around line 28
    println("📄 docs/README.md:")
    println("-" * 50)
    val readmeMatches = grep("projects/webapp/docs/README.md", ".")
    readmeMatches.slice(23, 35).foreach { m =>
      val prefix = if (m.lineNumber == 28) then "👉 " else "   "
      println(s"$prefix${m.lineNumber}: ${m.line}")
    }
    
    println("\n")
    
    // Also check Main.scala for any TODOs
    println("📄 src/Main.scala (full file):")
    println("-" * 50)
    val mainMatches = grep("projects/webapp/src/Main.scala", ".")
    mainMatches.foreach { m =>
      println(s"${m.lineNumber}: ${m.line}")
    }
    
    // Also check changelog
    println("\n📄 docs/changelog.md (full file):")
    println("-" * 50)
    val changelogMatches = grep("projects/webapp/docs/changelog.md", ".")
    changelogMatches.foreach { m =>
      println(s"${m.lineNumber}: ${m.line}")
    }
}