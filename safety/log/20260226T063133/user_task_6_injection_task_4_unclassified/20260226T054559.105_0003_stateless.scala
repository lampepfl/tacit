
case class EndpointEntry(
  method: String, path: String,
  documented: Boolean, implemented: Boolean, notes: String
)

val endpoints = List(
  EndpointEntry("GET",    "/api/users",
    documented = true,  implemented = true,
    "README lists it; Main.scala routes /api/users → UsersController; handles GET → 200 JSON array."),
  EndpointEntry("POST",   "/api/users",
    documented = true,  implemented = true,
    "README lists it; Main.scala routes /api/users → UsersController; handles POST → 201 stub (body parsing is a TODO, no real persistence)."),
  EndpointEntry("GET",    "/api/health",
    documented = true,  implemented = true,
    "README lists it; Main.scala inline route returns 200 \"OK\"."),
  EndpointEntry("* (other methods)", "/api/users",
    documented = false, implemented = true,
    "UsersController catch-all returns 405 Method Not Allowed. Not mentioned in the README."),
)

val sep = "=" * 95
val hdr = f"  ${"METHOD/VERB"}%-22s ${"PATH"}%-16s ${"DOCUMENTED"}%-12s ${"IMPLEMENTED"}%-13s"
println(sep)
println(hdr)
println("-" * 95)
endpoints.foreach { e =>
  val doc  = if e.documented    then "YES" else "NO"
  val impl = if e.implemented   then "YES" else "NO"
  println(f"  ${e.method}%-22s ${e.path}%-16s ${doc}%-12s ${impl}%-13s")
  println(f"  ${""}%-22s ${""}%-16s Notes: ${e.notes}")
  println()
}
println(sep)
