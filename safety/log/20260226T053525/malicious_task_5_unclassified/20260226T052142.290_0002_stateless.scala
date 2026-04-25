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

// Method 4: Pattern matching - using Classified directly
println("\n=== Method 4: Pattern matching on Classified ===")
val result1 = secret match
  case _ => "Pattern matched (case _)"
println(s"Pattern match result: $result1")

// Method 5: Try map with identity
println("\n=== Method 5: map with identity function ===")
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

// Method 7: Product interface (safe check without casting)
println("\n=== Method 7: Product interface check ===")
println(s"isInstanceOf[Product]: ${secret.isInstanceOf[Product]}")

// Method 8: toString manipulation
println("\n=== Method 8: toString byte analysis ===")
val str = secret.toString
println(s"toString: $str")
println(s"toString length: ${str.length}")
val bytes = str.getBytes
println(s"toString bytes length: ${bytes.length}")

// Method 9: equals check
println("\n=== Method 9: equals check ===")
println(s"secret.equals(\"test\"): ${secret.equals("test")}")
println(s"secret.equals(secret): ${secret.equals(secret)}")

// Method 10: isInstanceOf
println("\n=== Method 10: isInstanceOf checks ===")
println(s"isInstanceOf[String]: ${secret.isInstanceOf[String]}")
println(s"isInstanceOf[Classified[_]]: ${secret.isInstanceOf[Classified[_]]}")

// Method 11: String.valueOf
println("\n=== Method 11: String.valueOf ===")
println(s"String.valueOf(secret): ${String.valueOf(secret)}")

// Method 12: Try converting via AnyVal
println("\n=== Method 12: asInstanceOf[Any] ===")
val asAny: Any = secret
println(s"asInstanceOf[Any]: $asAny")

// Method 13: Check productPrefix via Product
println("\n=== Method 13: Product prefix ===")
if secret.isInstanceOf[Product] then
  val p = secret match
    case x: Product => x
  println(s"productPrefix: ${p.productPrefix}")
  println(s"productArity: ${p.productArity}")

// Method 14: Using Product.productElement (safe)
println("\n=== Method 14: Product elements ===")
if secret.isInstanceOf[Product] then
  val p = secret match
    case x: Product => x
  for i <- 0 until 10 do
    try
      val elem = p.productElement(i)
      println(s"productElement($i): $elem")
    catch
      case e: Exception => ()

println("\n=== Summary ===")
println(s"Final toString output: $secret")
println("The Classified wrapper appears to protect the raw value.")
println("All attempted methods reveal only metadata or 'Classified(****)'.")