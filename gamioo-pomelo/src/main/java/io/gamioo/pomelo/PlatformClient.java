package io.gamioo.pomelo;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * @author Allen Jiang
 */
public class PlatformClient {
    private static final Logger logger = LogManager.getLogger(PlatformClient.class);
    private final String hostName;
    private final int timeout;

    /**
     * @param hostName 域名
     * @param timeout  超时时间
     */
    public PlatformClient(String hostName, int timeout) {
        this.hostName = hostName;
        this.timeout = timeout;
    }


    public String getHostName() {
        return hostName;
    }


    public JSONObject get4https(String url) throws ServiceException {
        if (url == null) {
            url = "";
        }
        url = hostName + url;
        logger.info("url={}", url);
        CloseableHttpClient client = this.createSslInsecureClient();

        try {
            HttpGet httpGet = new HttpGet(url);
            // 设置建立连接超时时间
            // 设置读数据超时时间
            RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(timeout).setSocketTimeout(timeout)
                    .setConnectTimeout(timeout).build();
            httpGet.setConfig(config);
            String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 NetType/WIFI MicroMessenger/7.0.20.1781(0x6700143B) WindowsWechat(0x6305002e)";
            httpGet.setHeader("User-Agent", userAgent);
            // get responce
            // Create a custom response handler
            String finalUrl = url;
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity, Consts.UTF_8) : null;
                }
                return null;
            };
            long start = System.nanoTime();
            String ret = client.execute(httpGet, responseHandler);
            logger.info("recv delay={} ms", (System.nanoTime() - start) / 1000000f);
            if (ret != null) {
                JSONObject object = JSON.parseObject(ret);
                return object;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return null;

    }

    public JSONObject post4https(String url, Map<String, Object> content) throws ServiceException {
        if (url == null) {
            url = "";
        }
        url = hostName + url;
        logger.info("url={},content={}", url, content);
        CloseableHttpClient client = this.createSslInsecureClient();
        try {
            HttpPost httpPost = new HttpPost(url);
            StringEntity postEntity = new StringEntity(JSON.toJSONString(content), "UTF-8");
            httpPost.addHeader("Content-Type", "text/xml");
            httpPost.setEntity(postEntity);
            // 设置建立连接超时时间
            // 设置读数据超时时间
            RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(timeout).setSocketTimeout(timeout)
                    .setConnectTimeout(timeout).build();
            httpPost.setConfig(config);
            // get responce
            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity, Consts.UTF_8) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            long start = System.nanoTime();
            String ret = client.execute(httpPost, responseHandler);
            logger.info("recv delay={} ms", (System.nanoTime() - start) / 1000000f);
            if (ret != null) {
                JSONObject object = JSON.parseObject(ret);
                return object;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }


    public String post4https(String url, String content) throws ServiceException {
        if (url == null) {
            url = "";
        }
        url = hostName + url;
        CloseableHttpClient client = this.createSslInsecureClient();
        try {
            HttpPost httpPost = new HttpPost(url);
            StringEntity postEntity = new StringEntity(content, "UTF-8");
            httpPost.addHeader("Content-Type", "application/json");
            // httpPost.addHeader("Content-Type", "text/xml");
            // entity.setContentType("application/json");
            // httpPost.addHeader("User-Agent", "wxpay sdk java v1.0 " +
            httpPost.setEntity(postEntity);
            // 设置建立连接超时时间
            // 设置读数据超时时间
            RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(timeout).setSocketTimeout(timeout)
                    .setConnectTimeout(timeout).build();
            httpPost.setConfig(config);
            // get responce
            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity, Consts.UTF_8) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            long start = System.nanoTime();
            logger.info("recv delay={} ms", (System.nanoTime() - start) / 1000000f);
            String ret = client.execute(httpPost, responseHandler);
            return ret;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return null;

    }

    private CloseableHttpClient createSslInsecureClient() {
        try {
            //	String[] sslClientProtocols = {"TLSv1","TLSv1.1","TLSv1.2"};

            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    new NoopHostnameVerifier());
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return HttpClients.createDefault();
    }

    public JSONObject get4Json(String url, Map<String, Object> params) throws ServiceException {
        url = URlUtil.buildUrl(url, params);
        return this.get4ReturnJson(url);
    }

    public String put4String(String url, Map<String, Object> params) throws ServiceException {
        url = URlUtil.buildUrl(url, params);
        return this.put4ReturnString(url);
    }


    public String post4String(String url, Map<String, Object> params) throws ServiceException {
        url = URlUtil.buildUrl(url, params);
        return this.post4ReturnString(url);
    }

    public String get4String(String url, Map<String, Object> params) throws ServiceException {
        url = URlUtil.buildUrl(url, params);
        return this.get4ReturnString(url);
    }

    public String delete4String(String url, Map<String, Object> params) throws ServiceException {
        url = URlUtil.buildUrl(url, params);
        return this.delete4ReturnString(url);
    }

    public <T> T get(String url, Map<String, Object> params, Class<T> clazz) throws ServiceException {
        url = URlUtil.buildUrl(url, params);
        return this.get(url, clazz);
    }


    public JSONObject get4https(String url, Map<String, Object> params) throws ServiceException {
        url = URlUtil.buildUrl(url, params);
        return this.get4https(url);
    }

    /**
     * A {@link PoolingHttpClientConnectionManager} with maximum 100 connections
     * per route and a total maximum of 200 connections is used internally.
     */
    public <T> T get(String url, Class<T> clazz) throws ServiceException {
        url = hostName + url;
        logger.debug("url={}", url);
        Request request = Request.Get(url);

        long start = System.nanoTime();
        try {
            String ret = request.connectTimeout(timeout).socketTimeout(timeout).execute().returnContent().asString();
            logger.debug("recv delay={} ms", (System.nanoTime() - start) / 1000000f);
            if (ret != null) {
                T object = JSON.parseObject(ret, clazz);
                return object;
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * A {@link PoolingHttpClientConnectionManager} with maximum 100 connections
     * per route and a total maximum of 200 connections is used internally.
     */
    public JSONObject get4ReturnJson(String url) throws ServiceException {
        url = hostName + url;
        logger.info("url={}", url);
        Request request = Request.Get(url);
        long start = System.nanoTime();
        try {
            String ret = request.connectTimeout(timeout).socketTimeout(timeout).execute().returnContent().asString();
            logger.info("recv delay={} ms", (System.nanoTime() - start) / 1000000f);
            if (ret != null) {
                JSONObject object = JSON.parseObject(ret);
                return object;
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String get4ReturnString(String url) throws ServiceException {
        logger.info("url={}", url);
        Request request = Request.Get(url);
        long start = System.nanoTime();
        try {
            String ret = request.connectTimeout(timeout).socketTimeout(timeout).execute().returnContent().asString();
            logger.info("recv delay={} ms", (System.nanoTime() - start) / 1000000f);
            return ret;

        } catch (Exception e) {
            if (e.getMessage().indexOf("500") < 0) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    public String put4ReturnString(String url) throws ServiceException {
        Request request = Request.Put(url);
        long start = System.nanoTime();
        try {
            String ret = request.connectTimeout(timeout).socketTimeout(timeout).execute().returnContent().asString();
            logger.info("recv delay={} ms", (System.nanoTime() - start) / 1000000f);
            return ret;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String post4ReturnString(String url) throws ServiceException {
        Request request = Request.Post(url);
        long start = System.nanoTime();
        try {
            String ret = request.connectTimeout(timeout).socketTimeout(timeout).execute().returnContent().asString();
            logger.info("recv delay={} ms", (System.nanoTime() - start) / 1000000f);
            return ret;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String delete4ReturnString(String url) throws ServiceException {
        logger.debug("url={}", url);
        Request request = Request.Delete(url);
        long start = System.nanoTime();
        try {
            String ret = request.connectTimeout(timeout).socketTimeout(timeout).execute().returnContent().asString();
            logger.info("recv delay={} ms", (System.nanoTime() - start) / 1000000f);
            return ret;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}
