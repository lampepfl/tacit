requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  val resolvers = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo/django/urls/resolvers.py")
  val content = resolvers.read()
  
  // Fix the indentation and duplicate - use exact match
  val bad = "      def check(self):\n    def check(self):"
  val good = "    def check(self):"
  
  var newContent = content.replace(bad, good)
  
  // Now fix the entire RoutePattern class indentation
  val badPattern = """class RoutePattern(CheckURLMixin):
      regex = LocaleRegexDescriptor('_route')
  
      def __init__(self, route, name=None, is_endpoint=False):
          self._route = route
          self._regex_dict = {}
          self._is_endpoint = is_endpoint
          self.name = name
          self.converters = _route_to_regex(str(route), is_endpoint)[1]
  
      def match(self, path):
          match = self.regex.search(path)
          if match:
              # RoutePattern doesn't allow non-named groups so args are ignored.
              kwargs = match.groupdict()
              for key, value in kwargs.items():
                  converter = self.converters[key]
                  try:
                      kwargs[key] = converter.to_python(value)
                  except ValueError:
                      return None
                  except Http404:
                      raise Resolver404()
              return path[match.end():], (), kwargs
          return None"""
              
  val goodPattern = """class RoutePattern(CheckURLMixin):
    regex = LocaleRegexDescriptor('_route')

    def __init__(self, route, name=None, is_endpoint=False):
        self._route = route
        self._regex_dict = {}
        self._is_endpoint = is_endpoint
        self.name = name
        self.converters = _route_to_regex(str(route), is_endpoint)[1]

    def match(self, path):
        match = self.regex.search(path)
        if match:
            # RoutePattern doesn't allow non-named groups so args are ignored.
            kwargs = match.groupdict()
            for key, value in kwargs.items():
                converter = self.converters[key]
                try:
                    kwargs[key] = converter.to_python(value)
                except ValueError:
                    return None
                except Http404:
                    raise Resolver404()
            return path[match.end():], (), kwargs
        return None"""
  
  newContent = newContent.replace(badPattern, goodPattern)
  resolvers.write(newContent)
  println("Fixed")
}