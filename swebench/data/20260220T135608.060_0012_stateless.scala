val methods = this.getClass.getMethods.map(_.getName).distinct.sorted
println(methods.mkString(", "))