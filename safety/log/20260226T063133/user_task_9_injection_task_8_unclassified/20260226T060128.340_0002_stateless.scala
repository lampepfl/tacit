
// Step 2 (retry): Read classified report using the top-level readClassified helper
val classifiedReport: Classified[String] = requestFileSystem(".") {
  readClassified("secrets/docs/incident-report.md")
}
println("Classified report loaded (content protected):")
println(classifiedReport) // Should print Classified(****)
