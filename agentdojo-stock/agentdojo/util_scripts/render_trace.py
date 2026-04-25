#!/usr/bin/env python3
"""Render an AgentDojo trace JSON into a self-contained HTML file.

Usage:
    python util_scripts/render_trace.py <trace.json> [-o <out.html>]

If -o is omitted, writes next to the input with a .html suffix.
The output is a single file with inline CSS and no external dependencies.
"""

from __future__ import annotations

import argparse
import html
import json
import sys
from pathlib import Path
from typing import Any

CSS = """
:root {
  --fg: #1a1a1a;
  --fg-muted: #6a6a6a;
  --fg-faint: #9a9a9a;
  --bg: #fdfdfd;
  --bg-soft: #f5f5f4;
  --bg-code: #f4f3f0;
  --rule: #e5e3e0;
  --accent: #3a6ea5;
  --user: #3a6ea5;
  --assistant: #4a8050;
  --tool: #b8851e;
  --system: #8a8a8a;
  --danger: #b23a3a;
  --ok: #4a8050;
}
* { box-sizing: border-box; }
html, body { margin: 0; padding: 0; background: var(--bg); color: var(--fg); }
body {
  font: 15px/1.55 -apple-system, BlinkMacSystemFont, "Segoe UI", "Helvetica Neue", sans-serif;
  padding: 48px 24px 96px;
}
main { max-width: 820px; margin: 0 auto; }
h1 { font-size: 20px; font-weight: 600; margin: 0 0 4px; letter-spacing: -0.01em; }
h2 {
  font-size: 11px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.08em;
  color: var(--fg-muted); margin: 44px 0 12px; padding-bottom: 6px;
  border-bottom: 1px solid var(--rule);
}
h3 { font-size: 13px; font-weight: 600; margin: 16px 0 6px; color: var(--fg); }
a { color: var(--accent); }
code, pre, .mono { font-family: ui-monospace, SFMono-Regular, "SF Mono", Menlo, Consolas, monospace; font-size: 13px; }
pre {
  background: var(--bg-code); border: 1px solid var(--rule); border-radius: 4px;
  padding: 10px 12px; margin: 6px 0; overflow-x: auto; white-space: pre;
  line-height: 1.45;
}
.subtitle { color: var(--fg-muted); font-size: 13px; margin: 0 0 18px; }
.note { color: var(--fg-muted); font-size: 12px; font-style: italic; margin: 0 0 8px; }

.metadata { display: grid; grid-template-columns: max-content 1fr; gap: 4px 16px; margin: 0; font-size: 13px; }
.metadata dt { color: var(--fg-muted); font-weight: 400; }
.metadata dd { margin: 0; color: var(--fg); }
.metadata dd.mono { font-family: ui-monospace, SFMono-Regular, Menlo, monospace; font-size: 12px; }

.badge {
  display: inline-block; padding: 1px 8px; border-radius: 10px; font-size: 11px;
  font-weight: 500; letter-spacing: 0.02em;
}
.badge-danger { background: #fbeaea; color: var(--danger); }
.badge-ok { background: #eaf3ea; color: var(--ok); }
.badge-muted { background: var(--bg-soft); color: var(--fg-muted); }

.injection { margin: 14px 0; }
.injection .key { font-size: 12px; color: var(--fg-muted); font-family: ui-monospace, Menlo, monospace; margin-bottom: 4px; }

.task-id { font-size: 12px; color: var(--fg-muted); margin-bottom: 6px; }
.task-id.mono { font-family: ui-monospace, Menlo, monospace; }

.secure-output pre { background: #fff7ec; border-color: #ecd7a6; }

.message {
  margin: 20px 0; padding: 14px 16px 14px 18px;
  border-left: 3px solid var(--rule); border-radius: 0 4px 4px 0;
  background: transparent;
}
.message.user { border-left-color: var(--user); }
.message.assistant { border-left-color: var(--assistant); }
.message.tool { border-left-color: var(--tool); background: #fcfbf8; }
.message.system { border-left-color: var(--system); }

.msg-head { display: flex; align-items: baseline; gap: 10px; margin-bottom: 6px; }
.msg-idx { color: var(--fg-faint); font-size: 11px; font-family: ui-monospace, Menlo, monospace; }
.msg-role {
  font-size: 11px; text-transform: uppercase; letter-spacing: 0.08em; font-weight: 600;
}
.message.user .msg-role { color: var(--user); }
.message.assistant .msg-role { color: var(--assistant); }
.message.tool .msg-role { color: var(--tool); }
.message.system .msg-role { color: var(--system); }
.msg-meta { color: var(--fg-faint); font-size: 11px; font-family: ui-monospace, Menlo, monospace; }

.msg-content { white-space: pre-wrap; word-wrap: break-word; }
.msg-content.long-text { font-size: 14px; }

.tool-call { margin-top: 10px; }
.tool-call-head {
  display: flex; align-items: baseline; gap: 8px;
  font-size: 12px; color: var(--fg-muted); margin-bottom: 2px;
}
.tool-call-name { font-family: ui-monospace, Menlo, monospace; color: var(--fg); font-weight: 500; }
.tool-call-id { color: var(--fg-faint); font-size: 11px; }

details.system-prompt { margin-top: 4px; }
details.system-prompt > summary {
  cursor: pointer; color: var(--fg-muted); font-size: 12px; list-style: none;
}
details.system-prompt > summary::-webkit-details-marker { display: none; }
details.system-prompt > summary::before { content: "▸ "; font-size: 10px; }
details.system-prompt[open] > summary::before { content: "▾ "; }

.backend-trace { font-family: ui-monospace, Menlo, monospace; font-size: 12px; margin: 2px 0; }
.backend-trace .idx { color: var(--fg-faint); }
.backend-trace .fn { color: var(--accent); }

.empty { color: var(--fg-faint); font-style: italic; }
"""


def _esc(s: Any) -> str:
    return html.escape(str(s) if s is not None else "", quote=True)


def _content_to_text(content: Any) -> str:
    """Flatten OpenAI/Anthropic-style content blocks to plain text."""
    if content is None:
        return ""
    if isinstance(content, str):
        return content
    if isinstance(content, list):
        parts: list[str] = []
        for part in content:
            if isinstance(part, dict):
                t = part.get("type")
                if t == "text" or "content" in part:
                    parts.append(str(part.get("content", part.get("text", ""))))
                elif t == "tool_use":
                    parts.append(
                        f"[tool_use name={part.get('name')} "
                        f"args={json.dumps(part.get('input', {}), ensure_ascii=False)}]"
                    )
                elif t == "tool_result":
                    parts.append(
                        f"[tool_result tool_use_id={part.get('tool_use_id')}]\n"
                        + _content_to_text(part.get("content", ""))
                    )
                else:
                    parts.append(json.dumps(part, ensure_ascii=False))
            else:
                parts.append(str(part))
        return "\n".join(parts)
    if isinstance(content, dict):
        return json.dumps(content, ensure_ascii=False, indent=2)
    return str(content)


def _render_tool_call(call: dict, idx: int) -> str:
    fn = call.get("function") or call.get("name") or "<unknown>"
    call_id = call.get("id", "")
    args = call.get("args") or call.get("arguments") or {}
    if isinstance(args, str):
        try:
            args = json.loads(args)
        except Exception:
            pass

    head_parts = [f'<span class="tool-call-name">{_esc(fn)}</span>']
    if call_id:
        head_parts.append(f'<span class="tool-call-id">{_esc(call_id)}</span>')
    head = (
        f'<div class="tool-call-head"><span>call #{idx}</span>'
        + " ".join(head_parts)
        + "</div>"
    )

    if isinstance(args, dict) and "code" in args and len(args) == 1:
        body = f"<pre>{_esc(args['code'])}</pre>"
    elif isinstance(args, dict):
        body = f"<pre>{_esc(json.dumps(args, ensure_ascii=False, indent=2))}</pre>"
    else:
        body = f"<pre>{_esc(args)}</pre>"

    return f'<div class="tool-call">{head}{body}</div>'


def _render_message(i: int, msg: dict) -> str:
    role = msg.get("role", "?")
    role_class = role if role in ("user", "assistant", "tool", "system") else "system"

    meta_bits: list[str] = []
    if msg.get("tool_call_id"):
        meta_bits.append(f"tool_call_id={_esc(msg['tool_call_id'])}")
    if msg.get("name"):
        meta_bits.append(f"name={_esc(msg['name'])}")
    if msg.get("tool_name"):
        meta_bits.append(f"tool_name={_esc(msg['tool_name'])}")
    meta_html = (
        f'<span class="msg-meta">{" · ".join(meta_bits)}</span>' if meta_bits else ""
    )

    head = (
        f'<div class="msg-head">'
        f'<span class="msg-idx">[{i:>3}]</span>'
        f'<span class="msg-role">{_esc(role)}</span>'
        f"{meta_html}"
        f"</div>"
    )

    content_text = _content_to_text(msg.get("content"))
    content_html = ""
    if content_text.strip():
        if role == "system":
            content_html = (
                '<details class="system-prompt">'
                "<summary>system prompt</summary>"
                f'<pre>{_esc(content_text)}</pre>'
                "</details>"
            )
        elif role == "tool":
            content_html = f'<pre>{_esc(content_text)}</pre>'
        else:
            content_html = f'<div class="msg-content long-text">{_esc(content_text)}</div>'

    tool_calls_html = "".join(
        _render_tool_call(call, j) for j, call in enumerate(msg.get("tool_calls") or [])
    )

    return (
        f'<div class="message {role_class}">{head}{content_html}{tool_calls_html}</div>'
    )


def _render_injections(injections: dict) -> str:
    if not injections:
        return '<p class="empty">No injection payloads.</p>'
    out: list[str] = []
    for key, val in injections.items():
        out.append(
            f'<div class="injection">'
            f'<div class="key">{_esc(key)}</div>'
            f'<pre>{_esc(str(val).strip() or "(empty)")}</pre>'
            f"</div>"
        )
    return "\n".join(out)


def _find_user_prompt(messages: list) -> str | None:
    for msg in messages:
        if msg.get("role") == "user":
            text = _content_to_text(msg.get("content")).strip()
            if text:
                return text
    return None


def _render_user_task(data: dict, messages: list) -> str:
    task_id = data.get("user_task_id")
    prompt = _find_user_prompt(messages)
    head = (
        f'<div class="task-id mono">{_esc(task_id)}</div>' if task_id else ""
    )
    if prompt:
        body = f"<pre>{_esc(prompt)}</pre>"
    else:
        body = '<p class="empty">No user prompt found in messages.</p>'
    return head + body


def _render_injection_task(data: dict, injections: dict) -> str:
    task_id = data.get("injection_task_id")
    attack = data.get("attack_type")
    head_bits: list[str] = []
    if task_id:
        head_bits.append(f'<span class="mono">{_esc(task_id)}</span>')
    if attack:
        head_bits.append(f'<span style="color:var(--fg-muted)">via {_esc(attack)}</span>')
    head = (
        f'<div class="task-id">{" · ".join(head_bits)}</div>' if head_bits else ""
    )
    return head + _render_injections(injections)


def _render_metadata(data: dict) -> str:
    rows: list[tuple[str, str]] = []

    def add(label: str, value: str) -> None:
        rows.append((label, value))

    def badge(value: Any, danger_true: bool = True) -> str:
        if value is True:
            cls = "badge-danger" if danger_true else "badge-ok"
            return f'<span class="badge {cls}">true</span>'
        if value is False:
            return '<span class="badge badge-muted">false</span>'
        return _esc(value)

    if "pipeline_name" in data:
        add("pipeline", f'<span class="mono">{_esc(data["pipeline_name"])}</span>')
    if "suite_name" in data:
        add("suite", _esc(data["suite_name"]))
    if "user_task_id" in data:
        add("user task", f'<span class="mono">{_esc(data["user_task_id"])}</span>')
    if "injection_task_id" in data:
        add(
            "injection task",
            f'<span class="mono">{_esc(data["injection_task_id"])}</span>',
        )
    if "attack_type" in data:
        add("attack", _esc(data["attack_type"]))
    if "security" in data:
        add("security", badge(data["security"], danger_true=True))
    if "utility" in data:
        add("utility", badge(data["utility"], danger_true=False))
    if "duration" in data and data["duration"] is not None:
        add("duration", f'{float(data["duration"]):.1f}s')
    if data.get("error"):
        add("error", f'<span class="mono">{_esc(data["error"])}</span>')
    if "benchmark_version" in data:
        add("benchmark", _esc(data["benchmark_version"]))
    if "agentdojo_package_version" in data:
        add("package", _esc(data["agentdojo_package_version"]))
    if "evaluation_timestamp" in data:
        add("evaluated", _esc(data["evaluation_timestamp"]))
    if "tool_trace_source" in data:
        add("trace source", _esc(data["tool_trace_source"]))

    body = "\n".join(
        f"<dt>{label}</dt><dd>{value}</dd>" for label, value in rows
    )
    return f'<dl class="metadata">{body}</dl>'


def _render_backend_traces(traces: list | None) -> str:
    if not traces:
        return '<p class="empty">No backend tool traces.</p>'
    lines: list[str] = []
    for i, call in enumerate(traces):
        fn = call.get("function") or call.get("name", "?")
        args = call.get("args", {})
        lines.append(
            f'<div class="backend-trace">'
            f'<span class="idx">[{i:>3}]</span> '
            f'<span class="fn">{_esc(fn)}</span>'
            f'({_esc(json.dumps(args, ensure_ascii=False))})'
            f"</div>"
        )
    return "\n".join(lines)


def render_trace(data: dict) -> str:
    title_bits = [
        data.get("suite_name") or "?",
        data.get("user_task_id") or "?",
        data.get("injection_task_id") or "—",
    ]
    title = " / ".join(title_bits)
    pipeline = data.get("pipeline_name") or ""

    parts: list[str] = []
    parts.append(
        "<!doctype html>\n"
        '<html lang="en">\n'
        "<head>\n"
        '  <meta charset="utf-8">\n'
        '  <meta name="viewport" content="width=device-width, initial-scale=1">\n'
        f"  <title>{_esc(title)}</title>\n"
        f"  <style>{CSS}</style>\n"
        "</head>\n"
        "<body>\n"
        "<main>\n"
    )
    parts.append(f"<h1>{_esc(title)}</h1>")
    if pipeline:
        parts.append(f'<p class="subtitle">{_esc(pipeline)}</p>')

    parts.append("<h2>Metadata</h2>")
    parts.append(_render_metadata(data))

    messages = data.get("messages") or []

    parts.append("<h2>User task</h2>")
    parts.append(_render_user_task(data, messages))

    parts.append("<h2>Injection task</h2>")
    parts.append(_render_injection_task(data, data.get("injections") or {}))

    parts.append(f"<h2>Conversation <span style='color:var(--fg-faint);font-weight:400;text-transform:none;letter-spacing:0'>({len(messages)} messages)</span></h2>")
    for i, msg in enumerate(messages):
        parts.append(_render_message(i, msg))

    backend = data.get("backend_tool_traces")
    if backend is not None:
        parts.append(f"<h2>Backend tool traces <span style='color:var(--fg-faint);font-weight:400;text-transform:none;letter-spacing:0'>({len(backend)})</span></h2>")
        parts.append(_render_backend_traces(backend))

    secure = data.get("tacit_secure_output")
    if secure:
        parts.append('<section class="secure-output">')
        parts.append("<h2>Tacit secure output</h2>")
        parts.append('<p class="note">Written post-run; not fed back into the agent context.</p>')
        parts.append(f"<pre>{_esc(secure.rstrip())}</pre>")
        parts.append("</section>")

    parts.append("</main>\n</body>\n</html>\n")
    return "\n".join(parts)


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("input", type=Path, help="Path to the trace JSON file")
    ap.add_argument(
        "-o",
        "--output",
        type=Path,
        default=None,
        help="Output .html path (default: alongside input with .html suffix)",
    )
    args = ap.parse_args()

    if not args.input.exists():
        print(f"Input not found: {args.input}", file=sys.stderr)
        return 1

    with args.input.open() as f:
        data = json.load(f)

    rendered = render_trace(data)
    out_path = args.output or args.input.with_suffix(".html")
    out_path.write_text(rendered, encoding="utf-8")
    print(str(out_path))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
