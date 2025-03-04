package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = br.readLine();
            log.info("http Header : {} ", line);
            if(line==null) {
                return;
            }

            String[] tokens = line.split(" ");
            int contentLength = 0;

            while (!line.isEmpty()) {
                line = br.readLine();
                log.info("http Header : {} ", line);
                if(line.contains("Content-Length")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }

            String url = tokens[1];

            if(url.equals("/user/create")) {
                String body = IOUtils.readData(br, contentLength);
                Map<String, String> params = util.HttpRequestUtils.parseQueryString(body);

                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                log.info(user.toString());

            } else {
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = Files.readAllBytes(new File("./webapp" + tokens[1]).toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
