
// Maybe the classified flag is set on the *working dir* root (".") context
// Let's check an existing incident-report entry directly
requestFileSystem(".") {
  val ir = access("secrets/docs/incident-report.md")
  println(s"incident-report: isClassified=${ir.isClassified}")
  val er = access("secrets/docs/employee-reviews.md")
  println(s"employee-reviews: isClassified=${er.isClassified}")
  val proj = access("projects/webapp/docs/README.md")
  println(s"README: isClassified=${proj.isClassified}")
  // Try creating the target file first, then check
  val target = access("secrets/docs/security-recommendations.txt")
  target.write("test")
  println(s"After write: isClassified=${target.isClassified}, exists=${target.exists}")
  target.delete()
}
