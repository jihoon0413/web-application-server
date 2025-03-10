package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

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
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);
            String path = getDefaultPath(request.getPath());



            if(path.equals("/user/create")) {

                User user = new User(request.getParameter("userId"), request.getParameter("password"), request.getParameter("name"),request.getParameter("email"));
                DataBase.addUser(user);
                response.sendRedirect("/index.html");
            } else if(path.equals("/user/login")) {
                User user = DataBase.findUserById(request.getParameter("userId"));
                if(user != null && Objects.equals(user.getPassword(), request.getParameter("password"))) {
                    response.addHeader("Set-Cookie", "logined=true; Path=/");
                    response.sendRedirect("/index.html");
                } else {
                    response.sendRedirect("/user/login_failed.html");
                }
            } else if (path.equals("/user/list.html")) {
                if(!isLogin(request.getHeader("Cookie"))) {
                    response.sendRedirect("/user/login.html");
                    return;
                }

                Collection<User> users = DataBase.findAll();
                StringBuilder sb = new StringBuilder();
                sb.append("<table border='1'>");
                for (User user : users) {
                    sb.append("<tr>");
                    sb.append("<td>").append(user.getUserId()).append("</td>");
                    sb.append("<td>").append(user.getName()).append("</td>");
                    sb.append("<td>").append(user.getEmail()).append("</td>");
                    sb.append("</tr>");
                }
                sb.append("</table>");
                response.forwardBody(sb.toString());
            } else {
                response.forward(path);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private boolean isLogin(String line) {
        Map<String, String> cookies = HttpRequestUtils.parseCookies(line);

        String value = cookies.get("logined");
        if(value == null) {
            return false;

        }
        return Boolean.parseBoolean(cookies.get("logined"));
    }

    private String getDefaultPath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }
        return path;
    }

}
