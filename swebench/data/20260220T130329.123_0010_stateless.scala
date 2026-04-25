
requestExecPermission(Set("ls", "find")) {
  exec("ls", List(".")).stdout
}
