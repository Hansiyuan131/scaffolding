package com.yuanstack.sca.service.system.assembly.httpclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.MapUtils;
import com.yuanstack.sca.service.system.common.constants.ModelEnum;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @description: HttpClientUtils
 * @author: hansiyuan
 * @date: 2022/6/29 12:06 PM
 */
@Slf4j
public class HttpClientUtils {
    protected static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    private static final int CONNECT_TIMEOUT = 50000;
    private static final int REQUEST_TIMEOUT = 50000;
    private static final int SOCKET_TIMEOUT = 50000;

    private static final int MAX_TOTAL = 1200;
    private static final int MAX_PER_ROUTE = 1000;

    /**
     * ????????????????????????????????????(?????????)
     */
    private static final int maxLineLength = 0;

    private static final int KEEP_ALIVE = 60;

    private static final String SSL_PROTOCOL = "SSLv3";

    private static Registry<ConnectionSocketFactory> registry;
    private static ConnectionSocketFactory plainSF;
    private static LayeredConnectionSocketFactory sslSF;

    private static final CloseableHttpClient httpClient;

    private static final PoolingHttpClientConnectionManager cm;

    /**
     * keepAlive??????
     */
    private static ConnectionKeepAliveStrategy myStrategy = (response, context) -> {
        HeaderElementIterator it = new BasicHeaderElementIterator
                (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext()) {
            HeaderElement he = it.nextElement();
            String param = he.getName();
            String value = he.getValue();
            if (value != null && param.equalsIgnoreCase
                    ("timeout")) {
                return Long.parseLong(value) * 1000;
            }
        }

        return KEEP_ALIVE * 1000;//?????????????????????????????????????????????60s
    };

    static {
        //RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
        //try {
        //    SSLContext sslContext = SSLContext.getInstance(SSL_PROTOCOL);
        //    sslContext.init(null, new TrustManager[] {new TrustAnyTrustManager()},
        //        new java.security.SecureRandom());
        //    sslSF = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        //} catch (Exception e) {
        //    throw new IllegalStateException("createSSLHttpClient throws " + e.getClass().getSimpleName() + " !", e);
        //}
        //
        //plainSF = new PlainConnectionSocketFactory();
        //registryBuilder.register("http", plainSF);
        //registryBuilder.register("https", sslSF);
        //registry = registryBuilder.build();

        //???????????????
        cm = new PoolingHttpClientConnectionManager();
        //???????????????
        cm.setMaxTotal(MAX_TOTAL);
        //???????????????????????????
        cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);

        ConnectionConfig connConfig = ConnectionConfig
                .custom()
                .setMessageConstraints(
                        MessageConstraints.custom().setMaxLineLength(maxLineLength).build()).build();
        cm.setDefaultConnectionConfig(connConfig);

        //????????????http?????????
        httpClient = HttpClients.custom()
                .setKeepAliveStrategy(myStrategy)
                .setConnectionManager(cm)
                .setDefaultRequestConfig(getRequestConfig(null, null, null))
                .build();

        //??????????????????????????????
        new IdleConnectionMonitorThread(cm).start();
    }

    /**
     * get??????
     *
     * @param url
     * @param tokenMap
     * @return
     */
    public static HttpResponseDTO doGet(String url, Map<String, String> tokenMap) {
        HttpResponseDTO httpResponseDTO = null;
        CloseableHttpResponse response = null;

        try {
            // ??????httpGet??????????????????
            HttpGet httpGet = new HttpGet(url);

            // ??????????????????????????????
            if (tokenMap != null && !tokenMap.isEmpty()) {
                tokenMap.keySet().forEach(key -> httpGet.setHeader(key, tokenMap.get(key)));
            }

            // ????????????????????????
            RequestConfig requestConfig = getRequestConfig(null, null, null);
            // ???httpGet??????????????????
            httpGet.setConfig(requestConfig);
            // ??????get????????????????????????
            response = httpClient.execute(httpGet);

            return handlerResponse(response);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.doGet??????", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.doGet??????", e);
        } finally {
            // ????????????
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return httpResponseDTO;
    }

    /**
     * get??????
     * ??????????????????
     *
     * @param url
     * @param tokenMap
     * @return
     */
    public static HttpResponseDTO doGet(String url, Map<String, String> tokenMap, Integer connectTimeout, Integer requestTimeout, Integer socketTimeout) {
        HttpResponseDTO httpResponseDTO = null;
        CloseableHttpResponse response = null;

        try {
            // ??????httpGet??????????????????
            HttpGet httpGet = new HttpGet(url);

            // ??????????????????????????????
            if (tokenMap != null && !tokenMap.isEmpty()) {
                tokenMap.keySet().forEach(key -> httpGet.setHeader(key, tokenMap.get(key)));
            }

            // ????????????????????????
            RequestConfig requestConfig = getRequestConfig(connectTimeout, requestTimeout, socketTimeout);
            // ???httpGet??????????????????
            httpGet.setConfig(requestConfig);
            // ??????get????????????????????????
            response = httpClient.execute(httpGet);

            return handlerResponse(response);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.doGet??????", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.doGet??????", e);
        } finally {
            // ????????????
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return httpResponseDTO;
    }


    public static HttpResponseDTO doGetWitEntity(String url, String jsonBody) {
        return doGetWitEntity(url, null, jsonBody);
    }

    /**
     * get?????? ??????body
     *
     * @param url
     * @param tokenMap
     * @return
     */
    public static HttpResponseDTO doGetWitEntity(String url, Map<String, String> tokenMap, String jsonBody) {
        HttpResponseDTO httpResponseDTO = null;
        CloseableHttpResponse response = null;

        try {
            // ??????httpGet??????????????????
           HttpGetWithEntity httpGetWithEntity = new HttpGetWithEntity(url);

            // ??????????????????????????????
            if (tokenMap != null && !tokenMap.isEmpty()) {
                tokenMap.keySet().forEach(key -> httpGetWithEntity.setHeader(key, tokenMap.get(key)));
            }

            // ????????????????????????
            RequestConfig requestConfig = getRequestConfig(null, null, null);
            // ???httpGet??????????????????
            httpGetWithEntity.setConfig(requestConfig);

            //JSON BODY ??????
            if (StringUtils.isNotBlank(jsonBody)) {
                StringEntity se = new StringEntity(jsonBody, StandardCharsets.UTF_8.toString());
                se.setContentType("text/json");
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpGetWithEntity.setEntity(se);
            }

            // ??????get????????????????????????
            response = httpClient.execute(httpGetWithEntity);

            return handlerResponse(response);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.doGet??????", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.doGet??????", e);
        } finally {
            // ????????????
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return httpResponseDTO;
    }

    /**
     * post??????
     *
     * @param url
     * @param paramMap
     * @return
     */
    public static HttpResponseDTO doPost(String url, Map<String, Object> paramMap, Map<String, String> headerMap,
                                         String jsonBody) {
        HttpResponseDTO httpResponseDTO = null;
        CloseableHttpResponse httpResponse = null;

        // ??????httpPost??????????????????
        HttpPost httpPost = new HttpPost(url);
        // ????????????????????????
        RequestConfig requestConfig = getRequestConfig(null, null, null);
        // ???httpPost??????????????????
        httpPost.setConfig(requestConfig);

        // ???????????????
        if (!headerMap.containsKey(HTTP.CONTENT_TYPE)) {
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
        }
        //??????header
        if (headerMap != null && headerMap.size() > 0) {
            headerMap.keySet().forEach(key -> httpPost.addHeader(key, headerMap.get(key)));
        }

        try {
            //JSON BODY ??????
            if (StringUtils.isNotBlank(jsonBody)) {
                StringEntity se = new StringEntity(jsonBody, StandardCharsets.UTF_8.toString());
                se.setContentType("text/json");
                if (MapUtils.isNotEmpty(headerMap) && !headerMap.containsKey(HTTP.CONTENT_TYPE)) {
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                }
                httpPost.setEntity(se);
            }

            // ??????post????????????
            if (null != paramMap && paramMap.size() > 0) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                // ??????map??????entrySet????????????entity
                Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
                // ??????????????????????????????
                Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> mapEntry = iterator.next();
                    if (mapEntry.getValue() != null) {
                        nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
                    }
                }

                // ???httpPost??????????????????????????????
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            httpResponse = httpClient.execute(httpPost);
            return handlerResponse(httpResponse);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.doPost??????", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.doPost??????", e);
        } finally {
            // ????????????
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return httpResponseDTO;
    }

    /**
     * post??????
     * ??????????????????
     *
     * @param url
     * @param paramMap
     * @return
     */
    public static HttpResponseDTO doPost(String url, Map<String, Object> paramMap, Map<String, String> headerMap,
                                         String jsonBody, Integer connectTimeout, Integer requestTimeout, Integer socketTimeout) {
        HttpResponseDTO httpResponseDTO = null;
        CloseableHttpResponse httpResponse = null;

        // ??????httpPost??????????????????
        HttpPost httpPost = new HttpPost(url);
        // ????????????????????????
        RequestConfig requestConfig = getRequestConfig(connectTimeout, requestTimeout, socketTimeout);
        // ???httpPost??????????????????
        httpPost.setConfig(requestConfig);

        // ???????????????
        if (!headerMap.containsKey(HTTP.CONTENT_TYPE)) {
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
        }
        //??????header
        if (headerMap != null && headerMap.size() > 0) {
            headerMap.keySet().forEach(key -> httpPost.addHeader(key, headerMap.get(key)));
        }

        try {
            //JSON BODY ??????
            if (StringUtils.isNotBlank(jsonBody)) {
                StringEntity se = new StringEntity(jsonBody, StandardCharsets.UTF_8.toString());
                se.setContentType("text/json");
                if (MapUtils.isNotEmpty(headerMap) && !headerMap.containsKey(HTTP.CONTENT_TYPE)) {
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                }
                httpPost.setEntity(se);
            }

            // ??????post????????????
            if (null != paramMap && paramMap.size() > 0) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                // ??????map??????entrySet????????????entity
                Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
                // ??????????????????????????????
                Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> mapEntry = iterator.next();
                    if (mapEntry.getValue() != null) {
                        nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
                    }
                }

                // ???httpPost??????????????????????????????
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            httpResponse = httpClient.execute(httpPost);
            return handlerResponse(httpResponse);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.doPost??????", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.doPost??????", e);
        } finally {
            // ????????????
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return httpResponseDTO;
    }

    /**
     * @param url
     * @param paramMap
     * @param headerMap
     * @param jsonBody
     * @param connectTimeout
     * @param requestTimeout
     * @param socketTimeout
     * @return
     */
    public static HttpResponseDTO doPatch(String url, Map<String, Object> paramMap, Map<String, String> headerMap,
                                          String jsonBody, Integer connectTimeout, Integer requestTimeout, Integer socketTimeout) {
        HttpResponseDTO httpResponseDTO = null;
        CloseableHttpResponse httpResponse = null;

        // ??????httpPost??????????????????
        HttpPatch httpPatch = new HttpPatch(url);
        // ????????????????????????
        RequestConfig requestConfig = getRequestConfig(connectTimeout, requestTimeout, socketTimeout);
        // ???httpPost??????????????????
        httpPatch.setConfig(requestConfig);

        // ???????????????
        if (!headerMap.containsKey(HTTP.CONTENT_TYPE)) {
            httpPatch.addHeader(HTTP.CONTENT_TYPE, "application/json");
        }
        //??????header
        if (headerMap != null && headerMap.size() > 0) {
            headerMap.keySet().forEach(key -> httpPatch.addHeader(key, headerMap.get(key)));
        }

        try {
            //JSON BODY ??????
            if (StringUtils.isNotBlank(jsonBody)) {
                StringEntity se = new StringEntity(jsonBody, StandardCharsets.UTF_8.toString());
                se.setContentType("text/json");
                if (MapUtils.isNotEmpty(headerMap) && !headerMap.containsKey(HTTP.CONTENT_TYPE)) {
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                }
                httpPatch.setEntity(se);
            }

            // ??????post????????????
            if (null != paramMap && paramMap.size() > 0) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                // ??????map??????entrySet????????????entity
                Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
                // ??????????????????????????????
                Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> mapEntry = iterator.next();
                    if (mapEntry.getValue() != null) {
                        nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
                    }
                }

                // ???httpPost??????????????????????????????
                try {
                    httpPatch.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            httpResponse = httpClient.execute(httpPatch);
            return handlerResponse(httpResponse);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.doPost??????", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.doPost??????", e);
        } finally {
            // ????????????
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return httpResponseDTO;
    }

    public static HttpResponseDTO doPatch(String url, Map<String, Object> paramMap, Map<String, String> headerMap,
                                          String jsonBody) {
        HttpResponseDTO httpResponseDTO = null;
        CloseableHttpResponse httpResponse = null;

        // ??????httpPatch??????????????????
        HttpPatch httpPatch = new HttpPatch(url);
        // ????????????????????????
        RequestConfig requestConfig = getRequestConfig(null, null, null);
        // ???httpPatch??????????????????
        httpPatch.setConfig(requestConfig);

        // ???????????????
        if (!headerMap.containsKey(HTTP.CONTENT_TYPE)) {
            httpPatch.addHeader(HTTP.CONTENT_TYPE, "application/json");
        }
        //??????header
        if (headerMap != null && headerMap.size() > 0) {
            headerMap.keySet().forEach(key -> httpPatch.addHeader(key, headerMap.get(key)));
        }

        try {
            //JSON BODY ??????
            if (StringUtils.isNotBlank(jsonBody)) {
                StringEntity se = new StringEntity(jsonBody, StandardCharsets.UTF_8.toString());
                se.setContentType("text/json");
                if (MapUtils.isNotEmpty(headerMap) && !headerMap.containsKey(HTTP.CONTENT_TYPE)) {
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                }
                httpPatch.setEntity(se);
            }

            // ??????patch????????????
            if (null != paramMap && paramMap.size() > 0) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                // ??????map??????entrySet????????????entity
                Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
                // ??????????????????????????????
                Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> mapEntry = iterator.next();
                    if (mapEntry.getValue() != null) {
                        nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
                    }
                }

                // ???httpPatch??????????????????????????????
                try {
                    httpPatch.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            httpResponse = httpClient.execute(httpPatch);
            return handlerResponse(httpResponse);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.doPatch??????", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.doPatch??????", e);
        } finally {
            // ????????????
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return httpResponseDTO;
    }

    /**
     * delete??????
     *
     * @param url
     * @param tokenMap
     * @return
     */
    public static HttpResponseDTO doDelete(String url, Map<String, String> tokenMap) {
        HttpResponseDTO httpResponseDTO = null;
        CloseableHttpResponse response = null;

        try {
            // ??????httpGet??????????????????
            HttpDelete httpDelete = new HttpDelete(url);

            // ??????????????????????????????
            if (tokenMap != null && !tokenMap.isEmpty()) {
                tokenMap.keySet().forEach(key -> httpDelete.setHeader(key, tokenMap.get(key)));
            }

            // ????????????????????????
            RequestConfig requestConfig = getRequestConfig(null, null, null);
            // ???httpDelete??????????????????
            httpDelete.setConfig(requestConfig);
            // ??????httpDelete????????????????????????
            response = httpClient.execute(httpDelete);

            return handlerResponse(response);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.doDelete??????", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.doDelete??????", e);
        } finally {
            // ????????????
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return httpResponseDTO;
    }


    /**
     * ????????????
     *
     * @param url
     * @param headerMap
     * @param httpFileDTO
     * @return
     */
    public static HttpResponseDTO fileUpload(String url, Map<String, String> headerMap, HttpFileDTO httpFileDTO) {
        HttpResponseDTO httpResponseDTO = null;
        CloseableHttpResponse httpResponse = null;

        // ??????httpPost??????????????????
        HttpPost httpPost = new HttpPost(url);
        // ????????????????????????
        RequestConfig requestConfig = getRequestConfig(null, null, null);
        // ???httpPost??????????????????
        httpPost.setConfig(requestConfig);

        //??????header
        if (headerMap != null && headerMap.size() > 0) {
            headerMap.keySet().forEach(key -> httpPost.addHeader(key, headerMap.get(key)));
        }

        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            //???????????????????????????????????????contenttype????????????????????????????????????formdata??????
            builder.addBinaryBody(httpFileDTO.getParamName(), httpFileDTO.getInputStream(), ContentType.MULTIPART_FORM_DATA,
                    httpFileDTO.getFileName());

            httpPost.setEntity(builder.build());

            httpResponse = httpClient.execute(httpPost);
            return handlerResponse(httpResponse);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.fileUpload??????", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.fileUpload??????", e);
        } finally {
            // ????????????
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return httpResponseDTO;
    }

    private static RequestConfig getRequestConfig(Integer connectTimeout, Integer requestTimeout, Integer socketTimeout) {

        connectTimeout = (connectTimeout == null ? CONNECT_TIMEOUT : connectTimeout);
        requestTimeout = (requestTimeout == null ? REQUEST_TIMEOUT : requestTimeout);
        socketTimeout = (socketTimeout == null ? SOCKET_TIMEOUT : socketTimeout);
        return RequestConfig.custom().setConnectTimeout(connectTimeout)// ????????????????????????????????????
                .setConnectionRequestTimeout(requestTimeout)// ??????????????????????????????
                .setSocketTimeout(socketTimeout)// ????????????????????????????????????
                .build();
    }

    private static Map<String, String> handlerHeaders(Header[] headers) {
        Map<String, String> headerMap = null;
        if (headers != null && headers.length > 0) {
            headerMap = new HashMap<>();
            for (Header header : headers) {
                headerMap.put(header.getName(), header.getValue());
            }
        }

        return headerMap;
    }

    private static Map<String, String> handlerCookies(Header[] headers) {
        Map<String, String> cookieMap = null;
        if (headers != null && headers.length > 0) {
            cookieMap = new HashMap<>();
            for (Header header : headers) {
                if ("Set-Cookie".equals(header.getName())) {
                    String cookieStr = header.getValue();
                    if (StringUtils.isNotBlank(cookieStr)) {
                        String[] array = cookieStr.split(";");
                        for (String param : array) {
                            String[] paramArray = param.split("=");
                            if (paramArray.length > 1) {
                                cookieMap.put(paramArray[0], paramArray[1]);
                            } else {
                                cookieMap.put(paramArray[0], "");
                            }
                        }
                    }
                    break;
                }
            }
        }

        return cookieMap;
    }

    private static HttpResponseDTO handlerResponse(CloseableHttpResponse response) throws IOException {
        // ????????????????????????????????????
        HttpEntity entity = response.getEntity();
        String result = null;
        if (entity != null) {
            result = EntityUtils.toString(entity, "UTF-8");
        }

        HttpResponseDTO httpResponseDTO = new HttpResponseDTO();
        httpResponseDTO.setResponseStr(result);
        httpResponseDTO.setHeaderMap(handlerHeaders(response.getAllHeaders()));
        httpResponseDTO.setCookieMap(handlerCookies(response.getAllHeaders()));
        httpResponseDTO.setStatusCode(response.getStatusLine().getStatusCode());

        EntityUtils.consume(response.getEntity());

        return httpResponseDTO;
    }

    /**
     * ??????????????????
     */
    public static class IdleConnectionMonitorThread extends Thread {

        private final HttpClientConnectionManager connMgr;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            super();
            this.connMgr = connMgr;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(5000);
                        // Close expired connections
                        connMgr.closeExpiredConnections();
                        // Optionally, close connections
                        // that have been idle longer than 30 sec
                        connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                // terminate
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    public static void main(String[] args) {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("uid", "UID");
        paramMap.put("page", 1);
        paramMap.put("pageSize", 10);
        Map<String, String> headMap = new HashMap<>();
        headMap.put(HTTP.CONTENT_TYPE, "aa");
        HttpResponseDTO httpResponseDTO = doPost("https://paas.gwserver.wiiqq.com/member-center/api/coupons/list",
                null, headMap, JSON.toJSONString(paramMap));
        LogUtils.info(log, ModelEnum.COMMON_UTILS, httpResponseDTO.getResponseStr());

    }
}

