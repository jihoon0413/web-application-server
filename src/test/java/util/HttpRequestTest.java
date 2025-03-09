package util;

import org.junit.jupiter.api.Test;
import webserver.HttpRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    public void request_GET() throws Exception {
        InputStream in = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
        HttpRequest request = new HttpRequest(in);

        assert("GET").equals(request.getMethod());
        assert("/user/create").equals(request.getPath());
        assert("keep-alive").equals(request.getHeader("Connection"));
        assert("asdf").equals(request.getParameter("userId"));
    }

    @Test
    public void request_POST() throws Exception {
        InputStream in = new FileInputStream(new File(testDirectory + "Http_POST.txt"));

        HttpRequest request = new HttpRequest(in);

        assert("POST").equals(request.getMethod());
        assert("/user/create").equals(request.getPath());
        assert("keep-alive").equals(request.getHeader("Connection"));
        assert("asdf").equals(request.getParameter("userId"));
    }
}
