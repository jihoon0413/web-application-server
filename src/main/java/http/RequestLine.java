package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private HttpMethod method;
    private String path;
    private Map<String, String> parameter = new HashMap<>();

    public RequestLine(String requestLine) {

        String[] tokens = requestLine.split(" ");

        if(tokens.length != 3) {
            throw new IllegalArgumentException(requestLine + "형식에 맞지 않습니다.");
        }


        method = HttpMethod.valueOf(tokens[0]);
        path = tokens[1];

        if (path.contains("?")) {
            int index = tokens[1].indexOf("?");
            path = tokens[1].substring(0, index);
            String queryString = tokens[1].substring(index + 1);
            parameter = util.HttpRequestUtils.parseQueryString(queryString);
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParameter() {
        return parameter;
    }

}
