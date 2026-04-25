
// Check system properties for the classified root path
val classifiedProp = System.getProperty("tacit.classified.root")
val classifiedEnv  = System.getenv("TACIT_CLASSIFIED_ROOT")
val classifiedEnv2 = System.getenv("CLASSIFIED_ROOT")
println(s"prop: $classifiedProp")
println(s"env1: $classifiedEnv")
println(s"env2: $classifiedEnv2")

// Also check all system properties
val props = System.getProperties
val enum  = props.propertyNames()
while enum.hasMoreElements do
  val key = enum.nextElement().toString
  if key.contains("tacit") || key.contains("classified") || key.contains("secret") then
    println(s"  $key = ${props.getProperty(key)}")
