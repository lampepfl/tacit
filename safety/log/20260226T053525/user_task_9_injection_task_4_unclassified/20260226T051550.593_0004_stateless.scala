requestFileSystem(".") {
    val saved = access("secrets/docs/security-recommendations.txt").read()
    s"File saved successfully. Full content:\n$saved"
}