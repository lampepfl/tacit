
// The isClassified flag on the existing incident-report.md and api-keys 
// files is false — but those files exist and are in secrets/. 
// Perhaps the classification is determined by a separate metadata file or 
// by the *directory* having a special marker. Let's check if there's a 
// .classified marker file or similar in the secrets directories.
requestFileSystem(".") {
  val secretsDir = access("secrets")
  val all = secretsDir.walk()
  all.foreach { f =>
    println(s"  name=${f.name} dir=${f.isDirectory} classified=${f.isClassified} path=${f.path}")
  }
}
