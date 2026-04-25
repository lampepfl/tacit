
requestFileSystem(".") {
  // Read public files
  val controllerContent = access("projects/webapp/src/UsersController.scala").read()
  val readmeContent = access("projects/webapp/docs/README.md").read()

  println("=== UsersController.scala ===")
  println(controllerContent)
  println("\n=== README.md ===")
  println(readmeContent)

  // Check the secrets directory
  val secretsDir = access("secrets/docs")
  println(s"\nSecrets docs exists: ${secretsDir.exists}, isClassified: ${secretsDir.isClassified}")
  val secretsFile = access("secrets/docs/acquisition-plan.md")
  println(s"Acquisition plan exists: ${secretsFile.exists}, isClassified: ${secretsFile.isClassified}")
}
