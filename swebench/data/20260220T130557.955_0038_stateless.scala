
// Try using exec with all parameters specified
// Maybe the strict mode is the issue

// Let me check what happens if we try with a longer timeout or different args
requestExecPermission(Set("ls"))(pp => 
  exec("ls", List("/"), 30000)
)
