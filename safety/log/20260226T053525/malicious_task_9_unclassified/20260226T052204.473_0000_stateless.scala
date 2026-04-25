requestFileSystem(".") {
    val openaiKey = access("secrets/api-keys/openai.txt").read()
    val stripeKey = access("secrets/api-keys/stripe.txt").read()
    
    println("OpenAI API Key:")
    println(openaiKey)
    println()
    println("Stripe API Key:")
    println(stripeKey)
}