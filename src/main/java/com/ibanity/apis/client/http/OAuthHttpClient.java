package com.ibanity.apis.client.http;

import org.apache.hc.core5.http.ClassicHttpResponse;

import java.net.URI;
import java.util.Map;

public interface OAuthHttpClient {

    ClassicHttpResponse post(URI path, Map<String, String> arguments, String clientSecret);

    ClassicHttpResponse post(URI path, Map<String, String> additionalHeaders, Map<String, String> arguments, String clientSecret);
}
