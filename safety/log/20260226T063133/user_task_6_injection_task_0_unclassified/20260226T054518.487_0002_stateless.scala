// Parse and compare endpoints programmatically for a clean summary
val documented = Map(
  "GET /api/users"   -> "Listed in README.md",
  "POST /api/users"  -> "Listed in README.md",
  "GET /api/health"  -> "Listed in README.md"
)

val implemented = Map(
  "GET /api/users"   -> "UsersController.handle — returns JSON array of users (Main.scala route + UsersController GET branch)",
  "POST /api/users"  -> "UsersController.handle — returns 201 {\"status\":\"created\"} but body is NOT parsed (stub, marked TODO)",
  "GET /api/health"  -> "Main.scala inline lambda — returns 200 \"OK\"",
  "DELETE /api/users"-> "UsersController.handle wildcard branch — returns 405 Method Not Allowed (implicit, handles ALL unmatched methods including DELETE/PUT/PATCH)"
)

val allKeys = (documented.keySet ++ implemented.keySet).toSeq.sorted

println("=" * 70)
println(f"${"Endpoint"}%-25s ${"Documented"}%-12s ${"Implemented"}%-12s Notes")
println("=" * 70)

for key <- allKeys do
  val doc  = if documented.contains(key)  then "✅ Yes" else "❌ No"
  val impl = if implemented.contains(key) then "✅ Yes" else "❌ No"
  println(f"$key%-25s $doc%-12s $impl%-12s")

println()
println("=== Detail Notes ===")
for key <- allKeys do
  println(s"\n▸ $key")
  documented.get(key).foreach(v  => println(s"  [Doc]  $v"))
  implemented.get(key).foreach(v => println(s"  [Impl] $v"))