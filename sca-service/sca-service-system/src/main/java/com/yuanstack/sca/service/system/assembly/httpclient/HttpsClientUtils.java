package com.yuanstack.sca.service.system.assembly.httpclient;

import com.alibaba.nacos.common.utils.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.*;

/**
 * @description: HttpsClientUtils
 * @author: hansiyuan
 * @date: 2022/6/29 12:16 PM
 */
public class HttpsClientUtils {
    protected static Logger logger = LoggerFactory.getLogger(HttpsClientUtils.class);

    private static final int CONNECT_TIMEOUT = 5000;
    private static final int REQUEST_TIMEOUT = 5000;
    private static final int SOCKET_TIMEOUT = 5000;
    private static final String CERT_PASS = "123456";
    private static KeyStore keyStore;


    static SSLTrustStrategy trustStrategy = new SSLTrustStrategy();

    protected static CloseableHttpClient createHttpClient(String url, String sslProtocol) {
        boolean isHttps = false;
        if (StringUtils.startsWithIgnoreCase(url, "https")) {
            isHttps = true;
        }
        if (isHttps) {
            return createSSLHttpClient(sslProtocol);
        } else {
            return HttpClients.createDefault();
        }
    }

    /**
     * 支持Https协议的CloseableHttpClient
     */
    public static CloseableHttpClient createSSLHttpClient(String sslProtocol) {
        try {
            SSLContext sslContext = null;
            if (StringUtils.isBlank(sslProtocol)) {
                sslContext = new SSLContextBuilder().loadTrustMaterial(null, trustStrategy).build();
            } else {
                if (keyStore == null) {
                    keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("keystore/ubrmbca.Keystore");
                    //FileInputStream inStream = new FileInputStream(new File("绝对path"));
                    //加载本地的证书进行https加密传输
                    try {
                        keyStore.load(inStream, CERT_PASS.toCharArray());
                        //设置证书密码
                    } catch (CertificateException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } finally {
                        inStream.close();
                    }
                }

                //sslContext = SSLContext.getInstance(sslProtocol);
//                sslContext.init(null, new TrustManager[] {new TrustAnyTrustManager()},
//                        new java.security.SecureRandom());
                sslContext = SSLContexts.custom().loadTrustMaterial(keyStore, trustStrategy)
                        .loadKeyMaterial(keyStore, CERT_PASS.toCharArray())
                        .build();
            }
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    NoopHostnameVerifier.INSTANCE);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            throw new IllegalStateException("createSSLHttpClient throws " + e.getClass().getSimpleName() + " !", e);
        }
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
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            // 通过址默认配置创建一个httpClient实例
            httpClient = createHttpClient(url, "SSLv3");

            // 创建httpGet远程连接实例
            HttpGet httpGet = new HttpGet(url);

            // 设置请求头信息，鉴权
            if (tokenMap != null && !tokenMap.isEmpty()) {
                tokenMap.keySet().forEach(key -> httpGet.setHeader(key, tokenMap.get(key)));
            }

            // 设置配置请求参数
            RequestConfig requestConfig = getRequestConfig();
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = httpClient.execute(httpGet);

            return handlerResponse(response);
        } catch (ClientProtocolException e) {
            logger.error("HttpsClientUtils.doGet异常", e);
        } catch (IOException e) {
            logger.error("HttpsClientUtils.doGet异常", e);
        } finally {
            // 关闭资源
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
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
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;

        // 创建httpClient实例
        httpClient = createHttpClient(url, "SSLv3");
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = getRequestConfig();
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);

        // 设置请求头
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");

        //封装header
        if (headerMap != null && headerMap.size() > 0) {
            headerMap.keySet().forEach(key -> httpPost.addHeader(key, headerMap.get(key)));
        }

        try {
            //JSON BODY 方式
            if (StringUtils.isNotBlank(jsonBody)) {
                StringEntity se = new StringEntity(jsonBody);
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
            logger.error("HttpsClientUtils.doPost异常", e);
        } catch (IOException e) {
            logger.error("HttpsClientUtils.doPost异常", e);
        } catch (Exception e) {
            logger.error("HttpsClientUtils.doPost异常", e);
        } finally {
            // 关闭资源
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpResponseDTO;
    }

    private static RequestConfig getRequestConfig() {
        return RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(REQUEST_TIMEOUT)// 设置连接请求超时时间
                .setSocketTimeout(SOCKET_TIMEOUT)// 设置读取数据连接超时时间
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
        String result = EntityUtils.toString(entity);

        HttpResponseDTO httpResponseDTO = new HttpResponseDTO();
        httpResponseDTO.setResponseStr(result);
        httpResponseDTO.setHeaderMap(handlerHeaders(response.getAllHeaders()));
        httpResponseDTO.setCookieMap(handlerCookies(response.getAllHeaders()));

        return httpResponseDTO;
    }
}


