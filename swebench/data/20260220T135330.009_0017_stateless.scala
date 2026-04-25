
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15202/repo") {
  val validatorsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15202/repo/django/core/validators.py")
  val lines = validatorsFile.readLines().toList
  
  // Replace lines 128-143 (0-indexed: 127-142)
  // Build the new content
  val newContent = lines.slice(0, 127) ++ 
    List(
      "        else:",
      "            # Now verify IPv6 in the netloc part",
      "            try:",
      "                host_match = re.search(r'^\\[(.+)\\](?::\\d{1,5})?$', urlsplit(value).netloc)",
      "            except ValueError:",
      "                raise ValidationError(self.message, code=self.code, params={'value': value})",
      "            if host_match:",
      "                potential_ip = host_match[1]",
      "                try:",
      "                    validate_ipv6_address(potential_ip)",
      "                except ValidationError:",
      "                    raise ValidationError(self.message, code=self.code, params={'value': value})",
      "",
      "        # The maximum length of a full host name is 253 characters per RFC 1034",
      "        # section 3.1. It's defined to be 255 bytes or less, but this includes",
      "        # one byte for the length of the name and one byte for the trailing dot",
      "        # that's used to indicate absolute names in DNS.",
      "        try:",
      "            if len(urlsplit(value).hostname) > 253:",
      "                raise ValidationError(self.message, code=self.code, params={'value': value})",
      "        except ValueError:",
      "            raise ValidationError(self.message, code=self.code, params={'value': value})",
      ""
    ) ++
    lines.slice(144, lines.length)
  
  validatorsFile.write(newContent.mkString("\n"))
}
