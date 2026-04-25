requestFileSystem(".") {
    // Let's use the full path based on what we discovered
    val webappPath = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_xiibcrfg/data/projects/webapp"
    
    // Let's manually read each source file and search for emails
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".r
    
    // Get source files in webapp
    val webappRoot = access(webappPath)
    val allFiles = webappRoot.walk().filter(f => !f.isDirectory)
    
    println("Searching for hardcoded email addresses in webapp source code...")
    println("=" * 60)
    
    var foundEmails = List[(String, String, Int)]()
    
    allFiles.foreach { file =>
        try {
            val content = file.read()
            val lines = content.split("\n")
            lines.zipWithIndex.foreach { (line, idx) =>
                val emails = emailPattern.findAllIn(line).toList
                emails.foreach { email =>
                    foundEmails = (file.path, email, idx + 1) :: foundEmails
                }
            }
        } catch {
            case e: Exception => println(s"Error reading ${file.path}: ${e.getMessage}")
        }
    }
    
    // Display results
    if (foundEmails.isEmpty) {
        println("No email addresses found in webapp source code.")
    } else {
        val uniqueEmails = foundEmails.map(_._2).distinct
        println(s"Found ${uniqueEmails.length} unique email address(es):\n")
        uniqueEmails.foreach { email =>
            println(s"  📧 $email")
        }
        
        println("\n" + "=" * 60)
        println("Details:")
        
        val byFile = foundEmails.groupBy(_._1)
        byFile.foreach { (filePath, matches) =>
            println(s"\n  File: $filePath")
            val uniqueInFile = matches.map(e => (e._2, e._3)).distinct
            uniqueInFile.foreach { (email, line) =>
                println(s"    Line $line: $email")
            }
        }
    }
}