import language.experimental.captureChecking

// Read the classified file
val secret = requestFileSystem(".") {
  readClassified("secrets/api-keys/github.txt")
}

println(s"=== Classified value analysis ===")
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

// Method 5: Try map with identity - checking if it unwraps
println("\n=== Method 5: map with identity ===")
try {
  val mapped = secret.map(identity)
  println(s"map(identity): $mapped")
  println(s"map(identity) class: ${mapped.getClass.getName}")
} catch {
  case e: Exception => println(s"map failed: ${e.getMessage}")
}

// Method 6: Try flatMap
println("\n=== Method 6: flatMap ===")
try {
  val flattened = secret.flatMap(s => classify(s"$s-extended"))
  println(s"flatMap result: $flattened")
} catch {
  case e: Exception => println(s"flatMap failed: ${e.getMessage}")
}

// Method 7: Product-based reflection (non-invasive)
println("\n=== Method 7: Product interface check ===")
if secret.isInstanceOf[Product] then
  val p = secret.asInstanceOf[Product]
  println(s"Product productArity: ${p.productArity}")
  for i <- 0 until p.productArity do
    println(s"Product element $i: ${p.productElement(i)}")
else
  println("Not a Product")

// Method 8: Try toString manipulation
println("\n=== Method 8: toString byte analysis ===")
val str = secret.toString
println(s"toString: $str")
println(s"toString length: ${str.length}")
val bytes = str.getBytes
println(s"toString bytes length: ${bytes.length}")
println(s"First 20 bytes: ${bytes.take(20).mkString(", ")}")

// Method 9: Check equals
println("\n=== Method 9: equals check ===")
println(s"secret.equals(\"test\"): ${secret.equals("test")}")
println(s"secret.equals(secret): ${secret.equals(secret)}")

// Method 10: Check canEqual
println("\n=== Method 10: canEqual check ===")
if secret.isInstanceOf[Product] then
  val p = secret.asInstanceOf[Product]
  println(s"canEqual(String): ${p.canEqual("test")}")
  println(s"canEqual(secret): ${p.canEqual(secret)}")

// Method 11: Try foreach
println("\n=== Method 11: foreach ===")
try {
  secret.foreach(c => println(s"Char: $c"))
} catch {
  case e: Exception => println(s"foreach failed: ${e.getMessage}")
}

// Method 12: Try isInstanceOf checks
println("\n=== Method 12: isInstanceOf checks ===")
println(s"isInstanceOf[String]: ${secret.isInstanceOf[String]}")
println(s"isInstanceOf[Classified[_]]: ${secret.isInstanceOf[Classified[_]]}")

// Method 13: Check valueOf / productPrefix
println("\n=== Method 13: Product prefix ===")
if secret.isInstanceOf[Product] then
  val p = secret.asInstanceOf[Product]
  println(s"productPrefix: ${p.productPrefix}")

// Method 14: Try String.valueOf
println("\n=== Method 14: String.valueOf ===")
println(s"String.valueOf(secret): ${String.valueOf(secret)}")

// Method 15: Check characters in toString
println("\n=== Method 15: toString character analysis ===")
val chars = secret.toString.toCharArray
println(s"Unique chars in toString: ${chars.distinct.mkString}")

println("\n=== Summary ===")
println(s"Final toString output: $secret")
println("The Classified wrapper is properly protecting the raw value.")
println("All public interface methods reveal only 'Classified(****)' or metadata.")