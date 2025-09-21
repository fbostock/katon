package fjdb.mealplanner.web;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.URI;

public class WebUtils {

    /**
     * You can use URI.create(String uri) to create a URI object if you don't have one.
     */
    public static boolean attemptPost(String json, URI uri) {
        try {
            HttpPost httppost = new HttpPost(uri);
            httppost.setEntity(new StringEntity(json));
            httppost.setHeader("Content-type", "application/json");

            CloseableHttpClient httpclient = HttpClients.createDefault();

            CloseableHttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            return statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean attemptPost(byte[] serializedData, URI uri) {
        try {
            HttpPost httppost = new HttpPost(uri);
            httppost.setEntity(new ByteArrayEntity(serializedData));

            CloseableHttpClient httpclient = HttpClients.createDefault();
            CloseableHttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            return statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
