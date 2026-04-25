
// Now let's try calling requestFileSystem with our function
// We need to convert FileSystem => String to (FileSystem ^) ?=> String
// This might work if Scala can do the implicit conversion

requestFileSystem(".")(processFs)
