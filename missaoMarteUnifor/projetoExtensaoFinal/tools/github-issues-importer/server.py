from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from urllib.error import HTTPError, URLError
from urllib.parse import urlsplit
from urllib.request import Request, urlopen
import json


ROOT = Path(__file__).resolve().parent
API_PREFIX = "/api/"
GITHUB_API_ROOT = "https://api.github.com"
MAX_BODY_BYTES = 1_000_000
ALLOWED_METHODS = {"GET", "POST"}
FORWARDED_REQUEST_HEADERS = {
    "accept": "Accept",
    "authorization": "Authorization",
    "content-type": "Content-Type",
    "x-github-api-version": "X-GitHub-Api-Version",
}
FORWARDED_RESPONSE_HEADERS = {
    "content-type": "Content-Type",
    "link": "Link",
    "retry-after": "Retry-After",
    "x-ratelimit-limit": "X-RateLimit-Limit",
    "x-ratelimit-remaining": "X-RateLimit-Remaining",
    "x-ratelimit-reset": "X-RateLimit-Reset",
}


class ImporterHandler(SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=str(ROOT), **kwargs)

    def do_GET(self):
        if self.path.startswith(API_PREFIX):
            self.proxy_to_github()
        else:
            super().do_GET()

    def do_POST(self):
        if self.path.startswith(API_PREFIX):
            self.proxy_to_github()
        else:
            self.send_error(405, "POST is only available for the GitHub API proxy")

    def do_PUT(self):
        self.send_error(405, "Method not allowed")

    def do_PATCH(self):
        self.send_error(405, "Method not allowed")

    def do_DELETE(self):
        self.send_error(405, "Method not allowed")

    def proxy_to_github(self):
        if self.command not in ALLOWED_METHODS:
            self.send_error(405, "Method not allowed")
            return

        parsed = urlsplit(self.path)
        api_path = parsed.path[len(API_PREFIX):]
        if not api_path or any(part in {".", ".."} for part in api_path.split("/")):
            self.send_error(400, "Invalid GitHub API path")
            return

        try:
            body_length = int(self.headers.get("Content-Length", "0"))
        except ValueError:
            self.send_error(400, "Invalid content length")
            return
        if body_length < 0 or body_length > MAX_BODY_BYTES:
            self.send_error(413, "Request body is too large")
            return

        request_body = self.rfile.read(body_length) if body_length else None
        upstream_url = f"{GITHUB_API_ROOT}/{api_path}"
        if parsed.query:
            upstream_url = f"{upstream_url}?{parsed.query}"

        headers = {
            target: self.headers[source]
            for source, target in FORWARDED_REQUEST_HEADERS.items()
            if self.headers.get(source)
        }
        headers.setdefault("Accept", "application/vnd.github+json")
        headers.setdefault("X-GitHub-Api-Version", "2022-11-28")

        request = Request(upstream_url, data=request_body, headers=headers, method=self.command)
        try:
            with urlopen(request, timeout=30) as response:
                self.send_upstream_response(response.status, response.headers, response.read())
        except HTTPError as error:
            self.send_upstream_response(error.code, error.headers, error.read())
        except URLError as error:
            reason = str(error.reason)
            self.send_json_error(502, f"Não foi possível conectar ao GitHub: {reason}")
        except TimeoutError:
            self.send_json_error(504, "Tempo limite ao aguardar resposta do GitHub.")

    def send_upstream_response(self, status, upstream_headers, body):
        self.send_response(status)
        for source, target in FORWARDED_RESPONSE_HEADERS.items():
            value = upstream_headers.get(source)
            if value:
                self.send_header(target, value)
        self.send_header("Content-Length", str(len(body)))
        self.send_header("Cache-Control", "no-store")
        self.end_headers()
        if body:
            self.wfile.write(body)

    def send_json_error(self, status, message):
        body = json.dumps({"message": message}, ensure_ascii=False).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.send_header("Cache-Control", "no-store")
        self.end_headers()
        self.wfile.write(body)

    def end_headers(self):
        self.send_header("Cache-Control", "no-store")
        super().end_headers()


if __name__ == "__main__":
    address = ("127.0.0.1", 4173)
    server = ThreadingHTTPServer(address, ImporterHandler)
    print(f"Importador local disponível em http://{address[0]}:{address[1]}/")
    print("O proxy aceita apenas GET/POST para https://api.github.com e não registra o token.")
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nServidor encerrado.")
    finally:
        server.server_close()
