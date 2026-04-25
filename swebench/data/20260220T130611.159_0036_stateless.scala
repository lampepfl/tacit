// Try to understand the REPL behavior
// What if I just call the function differently - using apply
val rf = requestFileSystem("/tmp") _
println(s"rf: $rf, type: ${rf.getClass}")
