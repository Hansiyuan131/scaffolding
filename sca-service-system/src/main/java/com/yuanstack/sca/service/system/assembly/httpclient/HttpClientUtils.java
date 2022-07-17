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
     * 每一个请求行的最大行长度(坑已踩)
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
     * keepAlive策略
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

        return KEEP_ALIVE * 1000;//如果没有约定，则默认定义时长为60s
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

        //连接池配置
        cm = new PoolingHttpClientConnectionManager();
        //最大连接数
        cm.setMaxTotal(MAX_TOTAL);
        //每个路由最大连接数
        cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);

        ConnectionConfig connConfig = ConnectionConfig
                .custom()
                .setMessageConstraints(
                        MessageConstraints.custom().setMaxLineLength(maxLineLength).build()).build();
        cm.setDefaultConnectionConfig(connConfig);

        //创建定制http客户端
        httpClient = HttpClients.custom()
                .setKeepAliveStrategy(myStrategy)
                .setConnectionManager(cm)
                .setDefaultRequestConfig(getRequestConfig(null, null, null))
                .build();

        //启动定制清除过期连接
        new IdleConnectionMonitorThread(cm).start();
    }

    /**
     * get请求
     *
     * @param url
     * @param tokenMap
     * @return
     */
    public static HttpResponseDTO doGet(String url, Map<String, String> tokenMap) {
        HttpResponseDTO httpResponseDTO = null;
        CloseableHttpResponse response = null;

        try {
            // 创建httpGet远程连接实例
            HttpGet httpGet = new HttpGet(url);

            // 设置请求头信息，鉴权
            if (tokenMap != null && !tokenMap.isEmpty()) {
                tokenMap.keySet().forEach(key -> httpGet.setHeader(key, tokenMap.get(key)));
            }

            // 设置配置请求参数
            RequestConfig requestConfig = getRequestConfig(null, null, null);
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = httpClient.execute(httpGet);

            return handlerResponse(response);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.doGet异常", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.doGet异常", e);
        } finally {
            // 关闭资源
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
     * get请求
     * 超时时间支持
     *
     * @param url
     * @param tokenMap
     * @return
     */
    public static HttpResponseDTO doGet(String url, Map<String, String> tokenMap, Integer connectTimeout, Integer requestTimeout, Integer socketTimeout) {
        HttpResponseDTO httpResponseDTO = null;
        CloseableHttpResponse response = null;

        try {
            // 创建httpGet远程连接实例
            HttpGet httpGet = new HttpGet(url);

            // 设置请求头信息，鉴权
            if (tokenMap != null && !tokenMap.isEmpty()) {
                tokenMap.keySet().forEach(key -> httpGet.setHeader(key, tokenMap.get(key)));
            }

            // 设置配置请求参数
            RequestConfig requestConfig = getRequestConfig(connectTimeout, requestTimeout, socketTimeout);
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = httpClient.execute(httpGet);

            return handlerResponse(response);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.doGet异常", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.doGet异常", e);
        } finally {
            // 关闭资源
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
     * get请求 存在body
     *
     * @param url
     * @param tokenMap
     * @return
     */
    public static HttpResponseDTO doGetWitEntity(String url, Map<String, String> tokenMap, String jsonBody) {
        HttpResponseDTO httpResponseDTO = null;
        CloseableHttpResponse response = null;

        try {
            // 创建httpGet远程连接实例
           HttpGetWithEntity httpGetWithEntity = new HttpGetWithEntity(url);

            // 设置请求头信息，鉴权
            if (tokenMap != null && !tokenMap.isEmpty()) {
                tokenMap.keySet().forEach(key -> httpGetWithEntity.setHeader(key, tokenMap.get(key)));
            }

            // 设置配置请求参数
            RequestConfig requestConfig = getRequestConfig(null, null, null);
            // 为httpGet实例设置配置
            httpGetWithEntity.setConfig(requestConfig);

            //JSON BODY 方式
            if (StringUtils.isNotBlank(jsonBody)) {
                StringEntity se = new StringEntity(jsonBody, StandardCharsets.UTF_8.toString());
                se.setContentType("text/json");
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpGetWithEntity.setEntity(se);
            }

            // 执行get请求得到返回对象
            response = httpClient.execute(httpGetWithEntity);

            return handlerResponse(response);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.doGet异常", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.doGet异常", e);
        } finally {
            // 关闭资源
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
     * post请求
     *
     * @param url
     * @param paramMap
     * @return
     */
    public static HttpResponseDTO doPost(String url, Map<String, Object> paramMap, Map<String, String> headerMap,
                                         String jsonBody) {
        HttpResponseDTO httpResponseDTO = null;
        CloseableHttpResponse httpResponse = null;

        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = getRequestConfig(null, null, null);
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);

        // 设置请求头
        if (!headerMap.containsKey(HTTP.CONTENT_TYPE)) {
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
        }
        //封装header
        if (headerMap != null && headerMap.size() > 0) {
            headerMap.keySet().forEach(key -> httpPost.addHeader(key, headerMap.get(key)));
        }

        try {
            //JSON BODY 方式
            if (StringUtils.isNotBlank(jsonBody)) {
                StringEntity se = new StringEntity(jsonBody, StandardCharsets.UTF_8.toString());
                se.setContentType("text/json");
                if (MapUtils.isNotEmpty(headerMap) && !headerMap.containsKey(HTTP.CONTENT_TYPE)) {
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                }
                httpPost.setEntity(se);
            }

            // 封装post请求参数
            if (null != paramMap && paramMap.size() > 0) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                // 通过map集成entrySet方法获取entity
                Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
                // 循环遍历，获取迭代器
                Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> mapEntry = iterator.next();
                    if (mapEntry.getValue() != null) {
                        nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
                    }
                }

                // 为httpPost设置封装好的请求参数
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            httpResponse = httpClient.execute(httpPost);
            return handlerResponse(httpResponse);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.doPost异常", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.doPost异常", e);
        } finally {
            // 关闭资源
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
     * post请求
     * 超时时间支持
     *
     * @param url
     * @param paramMap
     * @return
     */
    public static HttpResponseDTO doPost(String url, Map<String, Object> paramMap, Map<String, String> headerMap,
                                         String jsonBody, Integer connectTimeout, Integer requestTimeout, Integer socketTimeout) {
        HttpResponseDTO httpResponseDTO = null;
        CloseableHttpResponse httpResponse = null;

        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = getRequestConfig(connectTimeout, requestTimeout, socketTimeout);
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);

        // 设置请求头
        if (!headerMap.containsKey(HTTP.CONTENT_TYPE)) {
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
        }
        //封装header
        if (headerMap != null && headerMap.size() > 0) {
            headerMap.keySet().forEach(key -> httpPost.addHeader(key, headerMap.get(key)));
        }

        try {
            //JSON BODY 方式
            if (StringUtils.isNotBlank(jsonBody)) {
                StringEntity se = new StringEntity(jsonBody, StandardCharsets.UTF_8.toString());
                se.setContentType("text/json");
                if (MapUtils.isNotEmpty(headerMap) && !headerMap.containsKey(HTTP.CONTENT_TYPE)) {
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                }
                httpPost.setEntity(se);
            }

            // 封装post请求参数
            if (null != paramMap && paramMap.size() > 0) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                // 通过map集成entrySet方法获取entity
                Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
                // 循环遍历，获取迭代器
                Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> mapEntry = iterator.next();
                    if (mapEntry.getValue() != null) {
                        nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
                    }
                }

                // 为httpPost设置封装好的请求参数
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            httpResponse = httpClient.execute(httpPost);
            return handlerResponse(httpResponse);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.doPost异常", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.doPost异常", e);
        } finally {
            // 关闭资源
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

        // 创建httpPost远程连接实例
        HttpPatch httpPatch = new HttpPatch(url);
        // 配置请求参数实例
        RequestConfig requestConfig = getRequestConfig(connectTimeout, requestTimeout, socketTimeout);
        // 为httpPost实例设置配置
        httpPatch.setConfig(requestConfig);

        // 设置请求头
        if (!headerMap.containsKey(HTTP.CONTENT_TYPE)) {
            httpPatch.addHeader(HTTP.CONTENT_TYPE, "application/json");
        }
        //封装header
        if (headerMap != null && headerMap.size() > 0) {
            headerMap.keySet().forEach(key -> httpPatch.addHeader(key, headerMap.get(key)));
        }

        try {
            //JSON BODY 方式
            if (StringUtils.isNotBlank(jsonBody)) {
                StringEntity se = new StringEntity(jsonBody, StandardCharsets.UTF_8.toString());
                se.setContentType("text/json");
                if (MapUtils.isNotEmpty(headerMap) && !headerMap.containsKey(HTTP.CONTENT_TYPE)) {
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                }
                httpPatch.setEntity(se);
            }

            // 封装post请求参数
            if (null != paramMap && paramMap.size() > 0) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                // 通过map集成entrySet方法获取entity
                Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
                // 循环遍历，获取迭代器
                Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> mapEntry = iterator.next();
                    if (mapEntry.getValue() != null) {
                        nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
                    }
                }

                // 为httpPost设置封装好的请求参数
                try {
                    httpPatch.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            httpResponse = httpClient.execute(httpPatch);
            return handlerResponse(httpResponse);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.doPost异常", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.doPost异常", e);
        } finally {
            // 关闭资源
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

        // 创建httpPatch远程连接实例
        HttpPatch httpPatch = new HttpPatch(url);
        // 配置请求参数实例
        RequestConfig requestConfig = getRequestConfig(null, null, null);
        // 为httpPatch实例设置配置
        httpPatch.setConfig(requestConfig);

        // 设置请求头
        if (!headerMap.containsKey(HTTP.CONTENT_TYPE)) {
            httpPatch.addHeader(HTTP.CONTENT_TYPE, "application/json");
        }
        //封装header
        if (headerMap != null && headerMap.size() > 0) {
            headerMap.keySet().forEach(key -> httpPatch.addHeader(key, headerMap.get(key)));
        }

        try {
            //JSON BODY 方式
            if (StringUtils.isNotBlank(jsonBody)) {
                StringEntity se = new StringEntity(jsonBody, StandardCharsets.UTF_8.toString());
                se.setContentType("text/json");
                if (MapUtils.isNotEmpty(headerMap) && !headerMap.containsKey(HTTP.CONTENT_TYPE)) {
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                }
                httpPatch.setEntity(se);
            }

            // 封装patch请求参数
            if (null != paramMap && paramMap.size() > 0) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                // 通过map集成entrySet方法获取entity
                Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
                // 循环遍历，获取迭代器
                Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> mapEntry = iterator.next();
                    if (mapEntry.getValue() != null) {
                        nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
                    }
                }

                // 为httpPatch设置封装好的请求参数
                try {
                    httpPatch.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            httpResponse = httpClient.execute(httpPatch);
            return handlerResponse(httpResponse);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.doPatch异常", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.doPatch异常", e);
        } finally {
            // 关闭资源
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
     * delete请求
     *
     * @param url
     * @param tokenMap
     * @return
     */
    public static HttpResponseDTO doDelete(String url, Map<String, String> tokenMap) {
        HttpResponseDTO httpResponseDTO = null;
        CloseableHttpResponse response = null;

        try {
            // 创建httpGet远程连接实例
            HttpDelete httpDelete = new HttpDelete(url);

            // 设置请求头信息，鉴权
            if (tokenMap != null && !tokenMap.isEmpty()) {
                tokenMap.keySet().forEach(key -> httpDelete.setHeader(key, tokenMap.get(key)));
            }

            // 设置配置请求参数
            RequestConfig requestConfig = getRequestConfig(null, null, null);
            // 为httpDelete实例设置配置
            httpDelete.setConfig(requestConfig);
            // 执行httpDelete请求得到返回对象
            response = httpClient.execute(httpDelete);

            return handlerResponse(response);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.doDelete异常", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.doDelete异常", e);
        } finally {
            // 关闭资源
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
     * 上传文件
     *
     * @param url
     * @param headerMap
     * @param httpFileDTO
     * @return
     */
    public static HttpResponseDTO fileUpload(String url, Map<String, String> headerMap, HttpFileDTO httpFileDTO) {
        HttpResponseDTO httpResponseDTO = null;
        CloseableHttpResponse httpResponse = null;

        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = getRequestConfig(null, null, null);
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);

        //封装header
        if (headerMap != null && headerMap.size() > 0) {
            headerMap.keySet().forEach(key -> httpPost.addHeader(key, headerMap.get(key)));
        }

        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            //绑定文件参数，传入文件流和contenttype，此处也可以继续添加其他formdata参数
            builder.addBinaryBody(httpFileDTO.getParamName(), httpFileDTO.getInputStream(), ContentType.MULTIPART_FORM_DATA,
                    httpFileDTO.getFileName());

            httpPost.setEntity(builder.build());

            httpResponse = httpClient.execute(httpPost);
            return handlerResponse(httpResponse);
        } catch (ClientProtocolException e) {
            logger.error("HttpClientUtils.fileUpload异常", e);
        } catch (IOException e) {
            logger.error("HttpClientUtils.fileUpload异常", e);
        } finally {
            // 关闭资源
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
        return RequestConfig.custom().setConnectTimeout(connectTimeout)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(requestTimeout)// 设置连接请求超时时间
                .setSocketTimeout(socketTimeout)// 设置读取数据连接超时时间
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
        // 从响应对象中获取响应内容
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
     * 空闲连接监控
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

