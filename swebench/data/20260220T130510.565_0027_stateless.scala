// Check if there's a given IOCapability in scope
def check[T](t: T)(using io: IOCapability) = t
check(42)
