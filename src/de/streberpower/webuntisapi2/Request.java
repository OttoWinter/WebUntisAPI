package de.streberpower.webuntisapi2;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes.WebUntisResult;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by Streberpower on 10.10.2015.
 */
public class Request {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    public static final Random random = new Random();
    public static CloseableHttpClient client = null;

    @SuppressWarnings("FieldCanBeLocal")
    private final String jsonrpc = "2.0";
    public Map<Object, Object> params;
    public String method;
    private int id = random.nextInt();

    public Request(String method) {
        this.params = new TreeMap<>();
        this.method = method;
    }

    public static String send(URL url, String sessionId, String payload) throws WebUntisConnectionException {
        CloseableHttpResponse response = null;
        String result = null;
        try {
            logger.debug("Sending request to {} with sessionId={}", url.toString(), sessionId);
            //Log.d(TAG, "send(url='%s', sessionId='%s', payload='%s')", url.toString(), sessionId, payload);
            if (client == null)
                client = setupClient();
            HttpPost post;
            post = new HttpPost(url.toURI());
            post.addHeader("Content-Type", "application/json-rpc");
            if (sessionId != null && !sessionId.equals(""))
                post.addHeader("Cookie", String.format("JSESSIONID=%s", sessionId));
            post.setEntity(new StringEntity(payload));
            response = client.execute(post);
            logger.debug("URL: {} Status: {}", url.toString(), response.getStatusLine());
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } catch (URISyntaxException e) {
            logger.error("Can't send post, because URL(" + url.toString() + ") can't be converted to URI", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("Can't set entity "+payload, e);
        } catch (IOException e) {
            logger.error("Can't send payload, maybe there's no connection", e);
        } finally {
            if (response != null) try {
                response.close();
            } catch (IOException e) {
                logger.error("Can't close response", e);
            }
        }
        if(result == null) throw new WebUntisConnectionException();
        return result;
    }

    public static <T> WebUntisResult<T> send(URL url, String sessionId, String payload, Type resultType, Gson gson) throws WebUntisConnectionException, WebUntisParseException {
        String send = send(url, sessionId, payload);
        if (send == null)
            return null;
        try {
            return gson.fromJson(send, resultType);
        } catch (JsonSyntaxException e) {
            logger.error("Received response with syntax error from webuntis: " + send, e);
            throw new WebUntisParseException();
        }
    }

    private static CloseableHttpClient setupClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        try {
            SSLContextBuilder builder1 = new SSLContextBuilder();
            builder1.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            });
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    builder1.build());
            builder.setSSLSocketFactory(sslsf);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }

        return builder.build();
    }
}
