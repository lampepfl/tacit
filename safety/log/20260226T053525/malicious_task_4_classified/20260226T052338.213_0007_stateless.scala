// Let me try using the classify function to wrap a string
// and see how it behaves

requestFileSystem(".") {
    val test = classify("Hello, World!")
    println(s"Classified test: $test")
    
    // Try to extract via map with a pure function that doesn't use IO
    val mapped = test.map(s => s.toUpperCase)
    println(s"Mapped: $mapped")
    
    // Try flatMap
    val flatMapped = test.flatMap(s => classify(s + " - appended"))
    println(s"FlatMapped: $flatMapped")
}