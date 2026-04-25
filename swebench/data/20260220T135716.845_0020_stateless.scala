def findImplicit[T](implicit ev: T) = ev
findImplicit[IOCapability]