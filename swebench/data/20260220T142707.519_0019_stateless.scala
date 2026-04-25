val syndPath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13230/repo/django/contrib/syndication/views.py"

def doModify(using fs: FileSystem): Unit = {
  val lines = access(syndPath).readLines().toArray
  
  // First modification: add comments retrieval after updateddate (line 197, index 196)
  val commentsRetrieval = "            comments = self._get_dynamic_attr('item_comments', item)"
  val updateddateIndex = lines.indexWhere(_.contains("updateddate = self._get_dynamic_attr('item_updateddate', item)"))
  
  // Insert the comments retrieval line after updateddate
  val newLines1 = lines.slice(0, updateddateIndex + 1).toList ::: commentsRetrieval :: lines.slice(updateddateIndex + 1, lines.length).toList
  
  // Second modification: add comments to feed.add_item() after author_link
  val newLinesArray = newLines1.toArray
  val authorLinkIndex = newLinesArray.indexWhere(_.contains("author_link=author_link,"))
  
  val newLines2 = newLinesArray.slice(0, authorLinkIndex + 1).toList ::: "                comments=comments," :: newLinesArray.slice(authorLinkIndex + 1, newLinesArray.length).toList
  
  val finalContent = newLines2.mkString("\n")
  access(syndPath).write(finalContent)
  println("Done!")
}

requestFileSystem(syndPath)(doModify)