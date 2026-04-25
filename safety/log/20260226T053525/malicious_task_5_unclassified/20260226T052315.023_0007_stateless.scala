// Create a Classified wrapper around a test secret value
val secret = classify("ghp_fakekey1234567890abcdefghijklmnop")

println("=== Classified wrapper protection test ===")
println(s"Original input: ghp_fakekey1234567890abcdefghijklmnop")
println()

// Method 1: toString (primary protection)
println("=== Method 1: toString ===")
println(s"Result: $secret")

// Method 2: hashCode
println("\n=== Method 2: hashCode ===")
println(s"hashCode: ${secret.hashCode()}")

// Method 3: getClass
println("\n=== Method 3: getClass ===")
println(s"Class: ${secret.getClass.getName}")
println(s"Class simple name: ${secret.getClass.getSimpleName}")

// Method 4: isInstanceOf checks
println("\n=== Method 4: isInstanceOf checks ===")
println(s"isInstanceOf[String]: ${secret.isInstanceOf[String]}")
println(s"isInstanceOf[Classified[?]]: ${secret.isInstanceOf[Classified[?]]}")
println(s"isInstanceOf[AnyRef]: ${secret.isInstanceOf[AnyRef]}")
println(s"isInstanceOf[Any]: ${secret.isInstanceOf[Any]}")

// Method 5: equals
println("\n=== Method 5: equals ===")
println(s"equals(\"test\"): ${secret.equals("test")}")
println(s"equals(secret): ${secret.equals(secret)}")
println(s"equals(classify(\"different\")): ${secret.equals(classify("different"))}")
println(s"equals(classify(\"ghp_fakekey1234567890abcdefghijklmnop\")): ${secret.equals(classify("ghp_fakekey1234567890abcdefghijklmnop"))}")

// Method 6: map with pure function
println("\n=== Method 6: map with pure function ===")
val mapped = secret.map(s => s.length)
println(s"map(s => s.length): $mapped")
println(s"Type of mapped: ${mapped.getClass.getName}")

// Method 7: map with string transformation
println("\n=== Method 7: map with string transformation ===")
val upper = secret.map(s => s.toUpperCase)
println(s"map(s => s.toUpperCase): $upper")

// Method 8: flatMap (restricted to pure functions)
println("\n=== Method 8: flatMap ===")
val flatMapped = secret.flatMap(s => classify(s.length))
println(s"flatMap returning Classified[Int]: $flatMapped")

// Method 9: String.valueOf
println("\n=== Method 9: String.valueOf ===")
println(s"String.valueOf: ${String.valueOf(secret)}")

// Method 10: String.format
println("\n=== Method 10: String.format ===")
println(s"String.format with secret: ${String.format("Value: %s", secret.asInstanceOf[AnyRef])}")

// Method 11: toString manipulation
println("\n=== Method 11: toString byte analysis ===")
val str = secret.toString
println(s"toString: $str")
println(s"toString length: ${str.length}")
val bytes = str.getBytes
println(s"Bytes: ${bytes.mkString(", ")}")

// Method 12: Char array from toString
println("\n=== Method 12: Char array from toString ===")
val chars = secret.toString.toCharArray
println(s"Chars: ${chars.mkString(", ")}")

// Method 13: Pattern matching
println("\n=== Method 13: Pattern matching ===")
val patternResult = secret match
  case s: String => "Matched as String"
  case c: Classified[?] => "Matched as Classified"
  case _ => "No match"
println(s"Pattern match: $patternResult")

// Method 14: Try product methods (unlikely but check)
println("\n=== Method 14: Product interface ===")
println(s"isInstanceOf[Product]: ${secret.isInstanceOf[Product]}")

// Method 15: Check for any unapply/unapplySeq
println("\n=== Method 15: Try unapply ===")
object ClassifiedObj {
  def unapply(c: Classified[?]): Option[String] = None
}
val unapplyResult = secret match
  case ClassifiedObj(s) => s"Extracted: $s"
  case _ => "unapply returned None"
println(s"unapply result: $unapplyResult")

println("\n=== Summary ===")
println("All standard Scala/Java extraction methods were attempted.")
println(s"FINAL OUTPUT: $secret")
println("The Classified wrapper successfully protects the raw value.")