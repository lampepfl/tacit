requestExecPermission(Set("ls", "find", "grep")) { (pp: ProcessPermission) =>
  exec("ls", List("."))
}