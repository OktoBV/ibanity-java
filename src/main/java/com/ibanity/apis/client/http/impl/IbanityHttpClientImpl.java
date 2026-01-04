package com.ibanity.apis.client.http.impl;

import com.ibanity.apis.client.http.IbanityHttpClient;
import com.ibanity.apis.client.http.handler.IbanityResponseHandler;
import lombok.NonNull;
import com.ibanity.apis.client.utils.StringUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import tools.jackson.core.JacksonException;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.ibanity.apis.client.utils.IbanityUtils.jsonMapper;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;

;

public class IbanityHttpClientImpl implements IbanityHttpClient {

    private final HttpClient httpClient;
    private final SSLContext sslContext;
    private final IbanityResponseHandler ibanityResponseHandler;

    public IbanityHttpClientImpl(HttpClient httpClient, SSLContext sslContext) {
        this.httpClient = httpClient;
        this.sslContext = sslContext;
        ibanityResponseHandler = new IbanityResponseHandler();
    }

    @Override
    public SSLContext sslContext() {
        return sslContext;
    }

    @Override
    public ClassicHttpResponse get(@NonNull URI path) {
        return get(path, null);
    }

    @Override
    public ClassicHttpResponse get(@NonNull URI path, String customerAccessToken) {
        return get(path, new HashMap<>(), customerAccessToken);
    }

    @Override
    public ClassicHttpResponse get(@NonNull URI path, @NonNull Map<String, String> additionalHeaders, String customerAccessToken) {
        HttpGet httpGet = new HttpGet(path);
        return execute(additionalHeaders, customerAccessToken, httpGet);
    }

    @Override
    public ClassicHttpResponse post(@NonNull URI path, @NonNull Object requestApiModel) {
        return post(path, requestApiModel, null);
    }

    @Override
    public ClassicHttpResponse post(@NonNull URI path, @NonNull Object requestApiModel, String customerAccessToken) {
        return post(path, requestApiModel, new HashMap<>(), customerAccessToken);
    }

    @Override
    public ClassicHttpResponse post(@NonNull URI path, @NonNull Object requestApiModel, @NonNull Map<String, String> additionalHeaders, String customerAccessToken) {
        try {
            HttpPost httpPost = new HttpPost(path);
            httpPost.setEntity(createEntityRequest(jsonMapper().writeValueAsString(requestApiModel)));
            return execute(additionalHeaders, customerAccessToken, httpPost);
        } catch (JacksonException exception) {
            throw new RuntimeException("An error occurred while converting object to json", exception);
        }
    }

    @Override
    public ClassicHttpResponse delete(@NonNull URI path) {
        return delete(path, null);
    }

    @Override
    public ClassicHttpResponse delete(@NonNull URI path, String customerAccessToken) {
        return delete(path, Collections.emptyMap(), customerAccessToken);
    }

    @Override
    public ClassicHttpResponse delete(@NonNull URI path, @NonNull Map<String, String> additionalHeaders, String customerAccessToken) {
        HttpDelete httpDelete = new HttpDelete(path);
        return execute(additionalHeaders, customerAccessToken, httpDelete);
    }

    @Override
    public ClassicHttpResponse patch(@NonNull URI path, @NonNull Object requestApiModel) {
        return patch(path, requestApiModel, null);
    }

    @Override
    public ClassicHttpResponse patch(@NonNull URI path, @NonNull Object requestApiModel, String customerAccessToken) {
        return patch(path, requestApiModel, new HashMap<>(), customerAccessToken);
    }

    @Override
    public ClassicHttpResponse patch(@NonNull URI path, @NonNull Object requestApiModel, @NonNull Map<String, String> additionalHeaders, String customerAccessToken) {
        try {
            HttpPatch httpPatch = new HttpPatch(path);
            httpPatch.setEntity(createEntityRequest(jsonMapper().writeValueAsString(requestApiModel)));
            return execute(additionalHeaders, customerAccessToken, httpPatch);
        } catch (JacksonException exception) {
            throw new RuntimeException("An error occurred while converting object to json", exception);
        }
    }

    @Override
    public HttpClient httpClient() {
        return httpClient;
    }

    private ClassicHttpResponse execute(@NonNull Map<String, String> additionalHeaders, String customerAccessToken, ClassicHttpRequest httpRequestBase) {
        try {
            addHeaders(customerAccessToken, additionalHeaders, httpRequestBase);
            return httpClient.execute(httpRequestBase, ibanityResponseHandler);
        } catch (IOException exception) {
            throw new RuntimeException("An error occurred while connecting to Ibanity", exception);
        }
    }

    private HttpEntity createEntityRequest(String baseRequest) {
        return new StringEntity(baseRequest, APPLICATION_JSON);
    }

    private void addHeaders(String customerAccessToken, Map<String, String> additionalHeaders, HttpRequest httpRequestBase) {
        addAuthorizationHeader(customerAccessToken, httpRequestBase);
        additionalHeaders.forEach(httpRequestBase::addHeader);
    }

    private void addAuthorizationHeader(String customerAccessToken, HttpRequest requestBase) {
        if (StringUtils.isNotBlank(customerAccessToken)) {
            requestBase.addHeader(new BasicHeader(AUTHORIZATION, "Bearer " + customerAccessToken));
        }
    }
}
