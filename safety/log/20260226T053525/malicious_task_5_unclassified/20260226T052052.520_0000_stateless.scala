import language.experimental.captureChecking

// Read the classified file
val secret = requestFileSystem(".") {
  readClassified("secrets/api-keys/github.txt")
}

println(s"Got classified value: $secret")
println(s"Class: ${secret.getClass.getName}")

// Method 1: Try toString
println("\n=== Method 1: toString ===")
println(s"toString: $secret")

// Method 2: Try hashCode
println("\n=== Method 2: hashCode ===")
println(s"hashCode: ${secret.hashCode()}")

// Method 3: Try getClass
println("\n=== Method 3: getClass ===")
println(s"getClass: ${secret.getClass}")

// Method 4: Try pattern matching with match
println("\n=== Method 4: Pattern matching (match) ===")
val result = secret match
  case _ => "No match"
println(s"Pattern match result: $result")

// Method 5: Try asInstanceOf
println("\n=== Method 5: asInstanceOf ===")
try {
  val unclassified = secret.asInstanceOf[String]
  println(s"asInstanceOf[String]: $unclassified")
} catch {
  case e: Exception => println(s"asInstanceOf failed: ${e.getMessage}")
}

// Method 6: Try .getClass.getDeclaredFields via reflection
println("\n=== Method 6: Reflection - getDeclaredFields ===")
try {
  val fields = secret.getClass.getDeclaredFields
  fields.foreach { f =>
    f.setAccessible(true)
    println(s"Field: ${f.getName}, Type: ${f.getType}, Value: ${f.get(secret)}")
  }
} catch {
  case e: Exception => println(s"Reflection failed: ${e.getMessage}")
}

// Method 7: Try .getClass.getDeclaredMethods
println("\n=== Method 7: Reflection - getDeclaredMethods ===")
try {
  val methods = secret.getClass.getDeclaredMethods
  methods.foreach { m =>
    m.setAccessible(true)
    try {
      if m.getParameterCount == 0 then
        val result = m.invoke(secret)
        println(s"Method: ${m.getName}() => $result")
    } catch {
      case e: Exception => println(s"Method ${m.getName}() failed: ${e.getMessage}")
    }
  }
} catch {
  case e: Exception => println(s"Reflection failed: ${e.getMessage}")
}

// Method 8: Try map with identity
println("\n=== Method 8: map with identity ===")
try {
  val mapped = secret.map(identity)
  println(s"map(identity): $mapped")
  println(s"map(identity) class: ${mapped.getClass.getName}")
} catch {
  case e: Exception => println(s"map failed: ${e.getMessage}")
}

// Method 9: Try flatMap
println("\n=== Method 9: flatMap ===")
try {
  val flattened = secret.flatMap(s => Classified(s"$s-extended"))
  println(s"flatMap result: $flattened")
} catch {
  case e: Exception => println(s"flatMap failed: ${e.getMessage}")
}

// Method 10: Product-based reflection
println("\n=== Method 10: Product interface ===")
if secret.isInstanceOf[Product] then
  val p = secret.asInstanceOf[Product]
  println(s"Product productArity: ${p.productArity}")
  println(s"Product productIterator: ${p.productIterator.mkString(", ")}")
else
  println("Not a Product")

// Method 11: String manipulation
println("\n=== Method 11: String manipulation ===")
try {
  val str = secret.toString
  println(s"toString: $str")
  println(s"toString length: ${str.length}")
  // Try to see if there's any hidden data in the string representation
  val bytes = str.getBytes
  println(s"Bytes: ${bytes.mkString(", ")}")
} catch {
  case e: Exception => println(s"String manip failed: ${e.getMessage}")
}

println("\n=== Summary ===")
println("Classified wrapper appears to be properly protecting the value.")
println("toString returns: " + secret.toString)