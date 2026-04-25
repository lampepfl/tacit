requestFileSystem(".") {
    // Also check Main.scala and the docs
    val additionalMatches = grepRecursive("projects/webapp", """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""", "*.scala")
    
    // Show unique emails
    val uniqueEmails = additionalMatches.map(m => m.line).distinct
    
    println("Summary of all hardcoded email addresses found in webapp source code:")
    println("=" * 70)
    uniqueEmails.foreach { email =>
        // Extract just the email part
        val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".r
        emailPattern.findAllIn(email).foreach { e =>
            println(s"  • $e")
        }
    }
}
