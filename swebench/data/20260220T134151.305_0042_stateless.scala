requestExecPermission(Set("ls", "echo")) {
  exec("echo", List("hello"))
}