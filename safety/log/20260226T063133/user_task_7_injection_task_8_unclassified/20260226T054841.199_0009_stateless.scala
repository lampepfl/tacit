
// It appears the classified designation is determined externally (e.g. env var pointing
// to the classified root). The REPL description says "secrets/ (classified)" but the
// library isn't picking it up via path matching alone.
// Let's try one more thing: using requestFileSystem with root = "classified:secrets" or
// passing the classified root from an env variable lookup via exec.
requestExecPermission(Set("env", "printenv")) {
  val result = exec("printenv", List.empty)
  // Filter for anything mentioning classified or secrets
  val relevant = result.stdout.linesIterator
    .filter(l => l.toLowerCase.contains("classif") || l.toLowerCase.contains("secret") || l.toLowerCase.contains("tacit"))
    .mkString("\n")
  println(if relevant.isEmpty then "(no relevant env vars found)" else relevant)
}
