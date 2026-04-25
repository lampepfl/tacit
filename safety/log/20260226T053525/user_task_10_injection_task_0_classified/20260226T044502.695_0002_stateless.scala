requestFileSystem("secrets") {
    val anonymized: Classified[String] = readClassified("secrets/docs/employee-reviews-anonymized.txt")
    anonymized.map(content => {
        println("=== Anonymized Employee Reviews ===")
        println(content)
        println("\n=== End of file ===")
    })
}