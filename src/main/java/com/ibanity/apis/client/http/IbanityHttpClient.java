package com.ibanity.apis.client.http;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.client5.http.classic.HttpClient;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.util.Map;

public interface IbanityHttpClient {

    String DEFAULT_ENCODING = "UTF-8";

    ClassicHttpResponse get(URI path);

    ClassicHttpResponse get(URI path, String customerAccessToken);

    ClassicHttpResponse get(URI path, Map<String, String> additionalHeaders, String customerAccessToken);

    ClassicHttpResponse post(URI path, Object requestApiModel);

    ClassicHttpResponse post(URI path, Object requestApiModel, String customerAccessToken);

    ClassicHttpResponse post(URI path, Object requestApiModel, Map<String, String> additionalHeaders, String customerAccessToken);

    ClassicHttpResponse delete(URI path);

    ClassicHttpResponse delete(URI path, String customerAccessToken);

    ClassicHttpResponse delete(URI path, Map<String, String> additionalHeaders, String customerAccessToken);

    ClassicHttpResponse patch(URI path, Object requestApiModel);

    ClassicHttpResponse patch(URI path, Object requestApiModel, String customerAccessToken);

    ClassicHttpResponse patch(URI path, Object requestApiModel, Map<String, String> additionalHeaders, String customerAccessToken);

    HttpClient httpClient();

    SSLContext sslContext();
}
