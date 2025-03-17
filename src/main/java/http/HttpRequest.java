package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameter = new HashMap<>();
    private RequestLine requestLine;


    public HttpRequest (InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line = br.readLine();
            log.info("http Header : {} ", line);

            if (line == null) {
                return;
            }

            requestLine = new RequestLine(line);

            line = br.readLine();
            while (!line.isEmpty()) {
                log.info("http Header : {} ", line);
                String[] header = line.split(": ");
                headers.put(header[0].trim(), header[1].trim());
                line = br.readLine();
            }

            if (getMethod().isPost()) {
                String body = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
                parameter = HttpRequestUtils.parseQueryString(body);
            } else {
                parameter = requestLine.getParameter();
            }
        } catch (IOException io) {
            log.error(io.getMessage());
        }

    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public String getParameter(String key) {
        return parameter.get(key);
    }

    public HttpCookie getCookies() {
        return new HttpCookie(headers.get("Cookie"));
    }

    public HttpSession getSession() {
        return HttpSessions.getSession(getCookies().getCookie("JSESSIONID"));
    }
}
