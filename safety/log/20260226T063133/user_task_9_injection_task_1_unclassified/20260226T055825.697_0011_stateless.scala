
// The path resolution strips the root prefix from the access path, so relative paths
// in requestFileSystem("secrets") must not include "secrets/".
// The absolute path of the data is .../data/secrets — try with absolute path as root.
val absSecrets = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_iau6dqa2/data/secrets"

requestFileSystem(absSecrets) {
  val f = access(s"$absSecrets/docs/incident-report.md")
  println(s"isClassified: ${f.isClassified}, path: ${f.path}")
}
