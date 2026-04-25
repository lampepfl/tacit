
requestFileSystem(".") {
  // Try the top-level writeClassified helper (path string, not FileEntry method)
  val classifiedAnalysis = classify(
    """placeholder"""
  )
  writeClassified("secrets/docs/incident-changelog-analysis.txt", classifiedAnalysis)
}
