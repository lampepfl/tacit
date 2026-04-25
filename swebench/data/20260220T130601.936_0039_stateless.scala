
// Fix the exec call - workingDir should be Option[String]
requestExecPermission(Set("ls"))(pp => 
  exec("ls", List("/"), None, 30000)
)
