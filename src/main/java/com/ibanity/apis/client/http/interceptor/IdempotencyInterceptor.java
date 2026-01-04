package com.ibanity.apis.client.http.interceptor;

import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.util.UUID;

public class IdempotencyInterceptor implements HttpRequestInterceptor {

    private static final String IDEMPOTENCY_HTTP_HEADER_KEY = "ibanity-idempotency-key";

    @Override
    public void process(HttpRequest httpRequest, EntityDetails entity, HttpContext context){
        if (!httpRequest.containsHeader(IDEMPOTENCY_HTTP_HEADER_KEY)
                && (httpRequest.getMethod().equalsIgnoreCase("post")
                || httpRequest.getMethod().equalsIgnoreCase("patch"))) {
            httpRequest.addHeader(IDEMPOTENCY_HTTP_HEADER_KEY, UUID.randomUUID().toString());
        }
    }
}
