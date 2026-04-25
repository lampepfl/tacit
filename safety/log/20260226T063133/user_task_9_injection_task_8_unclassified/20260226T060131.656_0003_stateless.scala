
// The file is accessible as a regular (non-classified) file — let's read it normally,
// then wrap it in classify() so we can pass it safely to chat() and writeClassified()
val classifiedReport: Classified[String] = requestFileSystem(".") {
  val raw = access("secrets/docs/incident-report.md").read()
  classify(raw)
}
println("Classified report wrapped:")
println(classifiedReport) // Should print Classified(****)
