package networking;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class WebApp {

    static final String STATIC_ROOT = "src/main/resources/static";

    static final Map<String, String> CONTENT_TYPES = new HashMap<>();
    static {
        CONTENT_TYPES.put("html", "text/html; charset=UTF-8");
        CONTENT_TYPES.put("js",   "application/javascript; charset=UTF-8");
        CONTENT_TYPES.put("css",  "text/css; charset=UTF-8");
        CONTENT_TYPES.put("png",  "image/png");
        CONTENT_TYPES.put("jpg",  "image/jpeg");
        CONTENT_TYPES.put("jpeg", "image/jpeg");
        CONTENT_TYPES.put("ico",  "image/x-icon");
        CONTENT_TYPES.put("json", "application/json; charset=UTF-8");
    }

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 35000;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("WebApp listening on port " + port);

        while (true) {
            try (Socket client = serverSocket.accept()) {
                handle(client);
            } catch (Exception e) {
                System.err.println("Request error: " + e.getMessage());
            }
        }
    }

    static void handle(Socket client) throws IOException {
        BufferedReader in  = new BufferedReader(new InputStreamReader(client.getInputStream()));
        OutputStream   out = client.getOutputStream();

        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isBlank()) return;
        System.out.println(requestLine);

        // Consume remaining headers
        while (in.ready()) in.readLine();

        String[] parts  = requestLine.split(" ");
        String method   = parts[0];
        String fullPath = parts.length > 1 ? parts[1] : "/";

        int qIdx   = fullPath.indexOf('?');
        String path  = qIdx >= 0 ? fullPath.substring(0, qIdx) : fullPath;
        String query = qIdx >= 0 ? fullPath.substring(qIdx + 1) : "";

        if (!method.equals("GET")) {
            sendError(out, 405, "Method Not Allowed");
            return;
        }

        // --- Hardcoded service routes ---
        switch (path) {
            case "/api/health" -> sendJson(out, "{\"status\":\"UP\"}");
            case "/api/time"   -> sendJson(out, "{\"time\":\""
                    + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\"}");
            case "/api/greeting" -> {
                String name = queryParam(query, "name");
                if (name == null || name.isBlank()) { sendError(out, 400, "Missing parameter: name"); return; }
                sendJson(out, "{\"greeting\":\"Hello, " + escapeJson(name) + "!\"}");
            }
            case "/api/square" -> {
                String val = queryParam(query, "value");
                if (val == null) { sendError(out, 400, "Missing parameter: value"); return; }
                try {
                    double n = Double.parseDouble(val);
                    sendJson(out, "{\"input\":" + n + ",\"square\":" + (n * n) + "}");
                } catch (NumberFormatException e) {
                    sendError(out, 400, "Invalid number: " + escapeJson(val));
                }
            }
            default -> serveStatic(out, path);
        }
    }

    // --- Static file serving with path traversal protection ---
    static void serveStatic(OutputStream out, String path) throws IOException {
        if (path.equals("/")) path = "/index.html";

        // Reject path traversal
        String normalized = Path.of(path).normalize().toString().replace("\\", "/");
        if (!normalized.startsWith("/") || normalized.contains("..")) {
            sendError(out, 400, "Bad Request");
            return;
        }

        File file = new File(STATIC_ROOT + normalized);
        if (!file.exists() || file.isDirectory()) {
            sendError(out, 404, "Not Found");
            return;
        }

        // Verify file is inside STATIC_ROOT
        String canonical = file.getCanonicalPath();
        String root      = new File(STATIC_ROOT).getCanonicalPath();
        if (!canonical.startsWith(root)) {
            sendError(out, 400, "Bad Request");
            return;
        }

        byte[] body = Files.readAllBytes(file.toPath());
        String ext  = normalized.contains(".") ? normalized.substring(normalized.lastIndexOf('.') + 1) : "";
        String ct   = CONTENT_TYPES.getOrDefault(ext.toLowerCase(), "application/octet-stream");

        String headers = "HTTP/1.1 200 OK\r\nContent-Type: " + ct
                + "\r\nContent-Length: " + body.length + "\r\n\r\n";
        out.write(headers.getBytes());
        out.write(body);
        out.flush();
    }

    static void sendJson(OutputStream out, String json) throws IOException {
        byte[] body = json.getBytes("UTF-8");
        String headers = "HTTP/1.1 200 OK\r\nContent-Type: application/json; charset=UTF-8"
                + "\r\nContent-Length: " + body.length + "\r\n\r\n";
        out.write(headers.getBytes());
        out.write(body);
        out.flush();
    }

    static void sendError(OutputStream out, int code, String message) throws IOException {
        String body    = "{\"error\":\"" + escapeJson(message) + "\"}";
        byte[] bytes   = body.getBytes("UTF-8");
        String status  = code + " " + message;
        String headers = "HTTP/1.1 " + status + "\r\nContent-Type: application/json; charset=UTF-8"
                + "\r\nContent-Length: " + bytes.length + "\r\n\r\n";
        out.write(headers.getBytes());
        out.write(bytes);
        out.flush();
    }

    static String queryParam(String query, String key) throws UnsupportedEncodingException {
        for (String pair : query.split("&")) {
            int eq = pair.indexOf('=');
            if (eq < 0) continue;
            String k = URLDecoder.decode(pair.substring(0, eq), "UTF-8");
            if (k.equals(key)) return URLDecoder.decode(pair.substring(eq + 1), "UTF-8");
        }
        return null;
    }

    static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
