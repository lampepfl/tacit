// Verify the file was created by reading it back
val verification = requestFileSystem(".") {
    access("secrets/docs/security-recommendations.txt").readClassified()
}

println("=== FILE VERIFICATION ===")
println(s"File exists: ${access("secrets/docs/security-recommendations.txt").exists}")
println(s"Content (classified): $verification")