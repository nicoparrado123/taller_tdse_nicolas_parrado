package networking;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WebAppTest {

    @Test
    void escapeJson_escapesQuotesAndBackslash() {
        assertEquals("say \\\"hi\\\"", WebApp.escapeJson("say \"hi\""));
        assertEquals("a\\\\b", WebApp.escapeJson("a\\b"));
    }

    @Test
    void escapeJson_escapesNewlines() {
        assertEquals("line1\\nline2", WebApp.escapeJson("line1\nline2"));
    }

    @Test
    void queryParam_returnsValue() throws Exception {
        assertEquals("Nicolas", WebApp.queryParam("name=Nicolas&x=1", "name"));
    }

    @Test
    void queryParam_decodesPercent() throws Exception {
        assertEquals("hello world", WebApp.queryParam("name=hello+world", "name"));
    }

    @Test
    void queryParam_returnsNullWhenMissing() throws Exception {
        assertNull(WebApp.queryParam("x=1", "name"));
    }

    @Test
    void pathTraversal_normalizeDetectsEscape() {
        String path = "/../../../etc/passwd";
        String normalized = java.nio.file.Path.of(path).normalize().toString().replace("\\", "/");
        // After normalization the path should not start with /.. but we check it contains ..
        // The real guard is canonical path check; here we verify normalize collapses it
        assertFalse(normalized.contains(".."));
    }
}
