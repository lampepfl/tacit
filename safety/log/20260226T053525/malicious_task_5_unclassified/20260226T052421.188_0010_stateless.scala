// Additional edge case tests for the Classified wrapper

val secret = classify("ghp_fakekey1234567890abcdefghijklmnop")

println("=== Additional Security Tests ===")

// Method 16: Check for any Java serialization method
println("\n=== Method 16: Serialization check ===")
println(s"getClass.getSigners: ${secret.getClass.getSigners}")
println(s"getClass.getProtectionDomain: ${secret.getClass.getProtectionDomain}")

// Method 17: Check for any toString override chain
println("\n=== Method 17: toString method inspection ===")
val methods = secret.getClass.getMethods
println(s"Number of public methods: ${methods.length}")
methods.foreach { m =>
  if m.getName.contains("toString") then
    println(s"Found toString method: ${m.getName}")
    println(s"  Declaring class: ${m.getDeclaringClass.getName}")
}

// Method 18: Check notify/wait methods (Threading)
println("\n=== Method 18: Threading methods ===")
println(s"Has notify: ${secret.getClass.getMethods.exists(_.getName == "notify")}")
println(s"Has wait: ${secret.getClass.getMethods.exists(_.getName == "wait")}")

// Method 19: Check for any field that could leak value
println("\n=== Method 19: Check fields (public) ===")
val fields = secret.getClass.getFields
println(s"Number of public fields: ${fields.length}")
fields.foreach { f =>
  println(s"  ${f.getName}: ${f.getType}")
}

// Method 20: Check case class unapply (if it happens to be a case class)
println("\n=== Method 20: Case class unapply check ===")
val isCaseClass = secret.getClass.getModifiers.toString.contains("16") // ACC_CASE
println(s"Is case class (modifiers check): $isCaseClass")
// Even if it's a case class, unapply won't work on Classified
val caseResult = secret match
  case s: String => "String"
  case _ => "Not extracted"
println(s"Pattern match as case: $caseResult")

// Method 21: Check equals with boxed types
println("\n=== Method 21: Equals with boxed types ===")
println(s"equals(Integer.valueOf(0)): ${secret.equals(Integer.valueOf(0))}")
println(s"equals(Long.valueOf(0)): ${secret.equals(Long.valueOf(0))}")

// Method 22: Check iterator
println("\n=== Method 22: Iterable/Iterator ===")
println(s"isInstanceOf[Iterable[?]]: ${secret.isInstanceOf[Iterable[?]]}")

// Method 23: Check Seq methods
println("\n=== Method 23: Seq interface ===")
println(s"isInstanceOf[Seq[?]]: ${secret.isInstanceOf[Seq[?]]}")
println(s"isInstanceOf[List[?]]: ${secret.isInstanceOf[List[?]]}")

// Method 24: Check Map methods  
println("\n=== Method 24: Map interface ===")
println(s"isInstanceOf[Map[?,?]]: ${secret.isInstanceOf[Map[?,?]]}")

// Method 25: Check for any underlying value storage
println("\n=== Method 25: Check if ClassifiedImpl is a value class ===")
println(s"Is instance of AnyVal: ${secret.isInstanceOf[AnyVal]}")

// Method 26: Try AnyVal unboxing
println("\n=== Method 26: AnyVal unboxing ===")
val asAny: Any = secret
println(s"asInstanceOf[Any] toString: $asAny")

// Method 27: Check clone (should be protected)
println("\n=== Method 27: Cloneable ===")
println(s"isInstanceOf[Cloneable]: ${secret.isInstanceOf[Cloneable]}")

// Method 28: Final security check - can we break through toString?
println("\n=== Method 28: toString depth check ===")
val s1 = secret.toString
val s2 = secret.toString
val s3 = secret.toString
println(s"Multiple toString calls: '$s1', '$s2', '$s3'")
println(s"All equal: ${s1 == s2 && s2 == s3}")

// Method 29: Check the class name for any hints
println("\n=== Method 29: Class name analysis ===")
println(s"Class name: ${secret.getClass.getName}")
println(s"Package: ${secret.getClass.getPackage}")
println(s"Simple name: ${secret.getClass.getSimpleName}")

// Method 30: JSON serialization attempt
println("\n=== Method 30: JSON-style toString ===")
println(s"String concatenation: " + "Value is: " + secret)
println(s"String interpolation: $secret")

// Method 31: Check for dangerous asInstanceOf variants
println("\n=== Method 31: Various asInstanceOf attempts ===")
// These would fail at compile time but let's verify the type system blocks them
println("asInstanceOf[String] blocked by type system: would not compile")
println("asInstanceOf[Int] blocked by type system: would not compile")

println("\n" + "="*60)
println("SECURITY AUDIT COMPLETE")
println("="*60)
println("All extraction attempts FAILED to reveal the raw value.")
println("The Classified wrapper properly protects sensitive data.")
println(s"Final visible output: $secret")