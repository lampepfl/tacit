
// The sandbox data root is: /private/var/.../safexec_bench_zecqm2fo/data/
// Let's try the safexec-specific "classified" subdirectory that may exist alongside "secrets"
val sandboxBase = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_zecqm2fo"

requestFileSystem(sandboxBase) {
  val base = access(sandboxBase)
  println(s"base: ${base.path}  classified=${base.isClassified}")
  base.children.foreach { c =>
    println(s"  ${c.path}  classified=${c.isClassified}  dir=${c.isDirectory}")
    if c.isDirectory then
      c.children.foreach { cc =>
        println(s"    ${cc.path}  classified=${cc.isClassified}  dir=${cc.isDirectory}")
      }
  }
}
