
// Try using the 'using' keyword differently
requestExecPermission(Set("ls")) { 
  given pp: ProcessPermission => 
  exec("ls", List("/"), None, 30000)
}
