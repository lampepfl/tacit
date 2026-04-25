"""A simple synchronous MCP client for STDIO servers using JSON-RPC 2.0."""

import json
import subprocess
import threading
from pathlib import Path
from typing import Any, Optional


class MCPClient:
    """Synchronous MCP client that communicates with a server over stdin/stdout."""

    def __init__(self, command: str, args: Optional[list[str]] = None, env: Optional[dict[str, str]] = None):
        self._command = command
        self._args = args or []
        self._env = env
        self._request_id = 0
        self._process: Optional[subprocess.Popen] = None
        self._read_lock = threading.Lock()
        self._write_lock = threading.Lock()

    def start(self) -> None:
        """Spawn the server process and perform the MCP initialize handshake."""
        self._process = subprocess.Popen(
            [self._command, *self._args],
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            env=self._env,
        )
        # MCP initialize handshake
        result = self._request("initialize", {
            "protocolVersion": "2024-11-05",
            "capabilities": {},
            "clientInfo": {"name": "tau2-mcp-client", "version": "0.1.0"},
        })
        self._notify("notifications/initialized", {})
        self._server_capabilities = result.get("capabilities", {})
        self._server_info = result.get("serverInfo", {})

    def stop(self) -> None:
        """Shut down the server process."""
        if self._process is None:
            return
        try:
            self._process.stdin.close()
        except Exception:
            pass
        try:
            self._process.wait(timeout=5)
        except subprocess.TimeoutExpired:
            self._process.kill()
            self._process.wait(timeout=2)
        self._process = None

    def list_tools(self) -> list[dict[str, Any]]:
        """List available tools on the server."""
        result = self._request("tools/list", {})
        return result.get("tools", [])

    def call_tool(self, name: str, arguments: Optional[dict[str, Any]] = None) -> Any:
        """Call a tool on the server and return the result."""
        result = self._request("tools/call", {
            "name": name,
            "arguments": arguments or {},
        })
        return result

    def _next_id(self) -> int:
        self._request_id += 1
        return self._request_id

    def _send(self, message: dict) -> None:
        """Send a JSON-RPC message to the server's stdin."""
        assert self._process and self._process.stdin
        data = json.dumps(message) + "\n"
        with self._write_lock:
            self._process.stdin.write(data.encode("utf-8"))
            self._process.stdin.flush()

    def _read_message(self) -> dict:
        """Read a single JSON-RPC message from the server's stdout."""
        assert self._process and self._process.stdout
        with self._read_lock:
            while True:
                line = self._process.stdout.readline()
                if not line:
                    raise ConnectionError("MCP server process closed stdout")
                line = line.decode("utf-8").strip()
                if line:
                    return json.loads(line)

    def _request(self, method: str, params: dict) -> dict:
        """Send a JSON-RPC request and wait for the response."""
        req_id = self._next_id()
        self._send({
            "jsonrpc": "2.0",
            "id": req_id,
            "method": method,
            "params": params,
        })
        # Read messages until we get a response matching our request id.
        # Notifications received in between are discarded.
        while True:
            msg = self._read_message()
            if "id" in msg and msg["id"] == req_id:
                if "error" in msg:
                    err = msg["error"]
                    raise RuntimeError(f"MCP error {err.get('code')}: {err.get('message')}")
                return msg.get("result", {})

    def _notify(self, method: str, params: dict) -> None:
        """Send a JSON-RPC notification (no response expected)."""
        self._send({
            "jsonrpc": "2.0",
            "method": method,
            "params": params,
        })

    def __enter__(self):
        self.start()
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.stop()
        return False


def create_safe_exec_client(facade: str, facade_mcp_port: str) -> MCPClient:
    """Create an MCPClient for the SafeExecMCP server."""
    jar_path = Path(__file__).resolve().parents[3] / "SafeExecMCP.jar"
    endpoint = f"http://localhost:{facade_mcp_port}/mcp"
    return MCPClient(command="java", args=["-jar", str(jar_path), "--stateful", "--use-facade", facade, "--facade-mcp-endpoint", endpoint])
