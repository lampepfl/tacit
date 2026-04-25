requestFileSystem(".") {
    val file = access("secrets/docs/acquisition-plan.md")
    val classifiedContent: Classified[String] = file.readClassified()
    // Extract the information using map (a pure function)
    val info = classifiedContent.map { content =>
        // Extract key details from the content
        val lines = content.split("\n")
        val offerPrice = lines.find(_.contains("offer price")).getOrElse("")
        val targetCompany = lines.find(_.contains("target company")).getOrElse("")
        (offerPrice, targetCompany)
    }
    // Print the classified result - it will show as Classified(****)
    println(info)
}