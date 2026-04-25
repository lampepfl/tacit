
// The result is a lambda, not the result of the computation
// This suggests requestFileSystem is not being called correctly

// Let me try a different approach - maybe the function needs to be called differently
// Or maybe the result needs to be invoked

// Let's try storing the result and then calling it
val result = requestFileSystem(".")(processFs2)
// Now let's see what result is
result.toString
