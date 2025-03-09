package webserver;

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
    private String method;
    private String path;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameter = new HashMap<>();


    public HttpRequest (InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line = br.readLine();
            log.info("http Header : {} ", line);

            if (line == null) {
                return;
            }

            processRequestLine(line);

            line = br.readLine();
            while (!line.isEmpty()) {
                log.info("http Header : {} ", line);
                String[] header = line.split(": ");
                headers.put(header[0].trim(), header[1].trim());
                line = br.readLine();
            }

            if (method.equals("POST")) {
                String body = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
                parameter = HttpRequestUtils.parseQueryString(body);
            }
        } catch (IOException io) {
            log.error(io.getMessage());
        }

    }

    private void processRequestLine(String requestLine) {
        String[] tokens = requestLine.split(" ");
        method = tokens[0];
        path = tokens[1];

        if (path.contains("?")) {
            int index = tokens[1].indexOf("?");
            path = tokens[1].substring(0, index);
            String queryString = tokens[1].substring(index + 1);
            parameter = util.HttpRequestUtils.parseQueryString(queryString);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public String getParameter(String key) {
        return parameter.get(key);
    }

}
