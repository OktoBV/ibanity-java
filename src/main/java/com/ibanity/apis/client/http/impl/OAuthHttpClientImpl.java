package com.ibanity.apis.client.http.impl;

import com.ibanity.apis.client.http.OAuthHttpClient;
import com.ibanity.apis.client.http.handler.IbanityResponseHandler;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static com.ibanity.apis.client.utils.StringUtils.isNotBlank;
import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;

public class OAuthHttpClientImpl implements OAuthHttpClient {

    private final String clientId;
    private final HttpClient httpClient;
    private final IbanityResponseHandler ibanityResponseHandler;

    public OAuthHttpClientImpl(String clientId, HttpClient httpClient) {
        this.clientId = clientId;
        this.httpClient = httpClient;
        this.ibanityResponseHandler = new IbanityResponseHandler();
    }

    @Override
    public ClassicHttpResponse post(URI path, Map<String, String> arguments, String clientSecret) {
        return post(path, new HashMap<>(), arguments, clientSecret);
    }

    @Override
    public ClassicHttpResponse post(URI path, Map<String, String> additionalHeaders, Map<String, String> arguments, String clientSecret) {
        HttpPost post = new HttpPost(path);
        arguments.put("client_id", clientId);
        post.setEntity(createEntity(arguments));
        return execute(additionalHeaders, clientSecret, post);
    }

    private HttpEntity createEntity(Map<String, String> arguments) {
        return new UrlEncodedFormEntity(
                arguments.entrySet().stream()
                        .map(a -> new BasicNameValuePair(a.getKey(), a.getValue()))
                        .collect(Collectors.toList()),
                UTF_8);
    }

    private ClassicHttpResponse execute(Map<String, String> additionalHeaders, String clientSecret, HttpUriRequestBase httpRequestBase) {
        try {
            addHeaders(clientSecret, additionalHeaders, httpRequestBase);
            return httpClient.execute(httpRequestBase, ibanityResponseHandler);
        } catch (IOException exception) {
            throw new RuntimeException("An error occurred while connecting to Ibanity", exception);
        }
    }

    private void addHeaders(String clientSecret, Map<String, String> additionalHeaders, HttpUriRequestBase httpRequestBase) {
        addAuthorizationHeader(clientId, clientSecret, httpRequestBase);
        additionalHeaders.forEach(httpRequestBase::addHeader);
    }

    private void addAuthorizationHeader(String clientId, String clientSecret, HttpUriRequestBase requestBase) {
        if(isNotBlank(clientId) && isNotBlank(clientSecret)) {
            String base64Encoded = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(UTF_8));
            requestBase.addHeader(new BasicHeader(AUTHORIZATION, "Basic " + base64Encoded));
        }
    }
}
