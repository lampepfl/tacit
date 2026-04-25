
class TestPerm extends ProcessPermission(Set("ls", "find"), false)

requestExecPermission(Set("ls", "find"))(op => exec("ls", List(".")).stdout)
