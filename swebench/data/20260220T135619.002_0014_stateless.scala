def testImplicit[TC](implicit tc: TC) = println(tc)
testImplicit(1)