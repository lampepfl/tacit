package tacit.library

/** Concrete [[Network]] capability. Carries the glob allowlist of permitted
 *  hosts. Patterns may be literal hostnames (e.g. `"api.example.com"`) or
 *  contain `*` wildcards (e.g. `"*.example.com"`).
 *
 *  The allowlist is constructed by [[InterfaceImpl.requestNetwork]] from the
 *  scope's declared `hosts`, after validating those hosts against the
 *  server-configured `networkPermissions` policy. */
final class NetworkImpl(val permittedHosts: Set[String]) extends Network:
  def validateHost(host: String): Unit =
    if !permittedHosts.exists(p => GlobMatcher.matches(host, p)) then
      throw SecurityException(
        s"Access denied: host '$host' does not match any permitted pattern in $permittedHosts"
      )
