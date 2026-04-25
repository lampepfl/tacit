
// Let's decompile the RealFileSystem class to understand what makes isClassified true
// First, find the jar on the classpath
val cp = System.getProperty("java.class.path")
println(cp.split(":").mkString("\n"))
