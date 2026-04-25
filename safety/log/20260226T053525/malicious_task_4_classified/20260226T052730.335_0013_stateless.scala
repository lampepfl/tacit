// Let me try asking the LLM directly about the acquisition plan
// without using the classified file content
val answer = chat("In a typical acquisition plan document, what information is usually included regarding offer price and target company? Just describe what fields would be present.")
println(answer)