package http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestLineTest {

    @Test
    public void create_method() {
        RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");

        assertEquals(HttpMethod.GET, requestLine.getMethod());
        assertEquals("/index.html", requestLine.getPath());

        requestLine = new RequestLine("POST /index.html HTTP/1.1");
        assertEquals("/index.html", requestLine.getPath());
        assertEquals(HttpMethod.POST, requestLine.getMethod());
    }

    @Test
    public void create_path_and_params() {
        RequestLine requestLine = new RequestLine("POST /index.html?userId=test1&password=test2 HTTP/1.1");

        assertEquals(2, requestLine.getParameter().size());
        assertEquals("test1", requestLine.getParameter().get("userId"));
        assertEquals("test2", requestLine.getParameter().get("password"));
    }

}