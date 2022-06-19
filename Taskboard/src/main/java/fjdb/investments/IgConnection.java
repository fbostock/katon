package fjdb.investments;

//import java.net.http.HttpClient;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class IgConnection {

        //Api key for name MacTestKey 816f73829753a42b1177b07a89174636d3855eb9
  //

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    public static void main(String[] args) throws Exception {

        IgConnection obj = new IgConnection();

        obj.createIgSession();
//        System.out.println("Testing 1 - Send Http GET request");
//        obj.sendGet();

//        System.out.println("Testing 2 - Send Http POST request");
//        obj.sendPost();

    }

    private void sendGet() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://httpbin.org/get"))
                .setHeader("User-Agent", "Java 11 HttpClient Bot")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // print status code
        System.out.println(response.statusCode());

        // print response body
        System.out.println(response.body());

    }

    private void createIgSession() throws Exception {

        // form parameters
        Map<Object, Object> data = new HashMap<>();
        data.put("identifier", "FBostock");
        data.put("password", "j1v1nDiamond");
//        data.put("custom", "secret");
//        data.put("ts", System.currentTimeMillis());

//        String body = "{\"identifier\": \"francis.bostock@gmail.com\", \"password\": \"j1v1nDiamond\"}";
        String body = "{\"identifier\": \"fbostock\", \"password\": \"j1v1nDiamond\"}";
        HttpRequest request = HttpRequest.newBuilder()
//                .POST(buildFormDataFromMap(data))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create("https://api.ig.com/gateway/deal/session"))
                .setHeader("Content-Type", "application/json; charset=UTF-8") // add request header
//                .header("Accept", "application/json; charset=UTF-8")
                .header("VERSION", "2")
                .header("IG-ACCOUNT-ID", "BYEW3")
//                .header("IG-ACCOUNT-ID", "BYF04")
                .header("X-IG-API-KEY", "816f73829753a42b1177b07a89174636d3855eb9")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // print status code
        System.out.println(response.statusCode());

        // print response body
        System.out.println(response.body());
        System.out.println("Done");
    }

    private void sendPost() throws Exception {

        // form parameters
        Map<Object, Object> data = new HashMap<>();
        data.put("username", "abc");
        data.put("password", "123");
        data.put("custom", "secret");
        data.put("ts", System.currentTimeMillis());

        HttpRequest request = HttpRequest.newBuilder()
                .POST(buildFormDataFromMap(data))
                .uri(URI.create("https://httpbin.org/post"))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // print status code
        System.out.println(response.statusCode());

        // print response body
        System.out.println(response.body());

    }

    private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        System.out.println(builder.toString());
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }



}
