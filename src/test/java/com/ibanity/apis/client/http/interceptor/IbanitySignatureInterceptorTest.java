package com.ibanity.apis.client.http.interceptor;

import com.ibanity.apis.client.http.service.IbanityHttpSignatureService;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IbanitySignatureInterceptorTest {

    private static final String BASE_PATH = "https://api.ibanity.localhost";

    private IbanitySignatureInterceptor ibanitySignatureInterceptor;

    @Mock
    private IbanityHttpSignatureService ibanityHttpSignatureService;

    @Mock
    private HttpUriRequestBase httpRequest;

    @Mock
    private HttpContext httpContext;

    @Mock
    private EntityDetails entity;

    @BeforeEach
    void setUp() {
        ibanitySignatureInterceptor = new IbanitySignatureInterceptor(ibanityHttpSignatureService, BASE_PATH);
    }

    @Test
    void process() throws IOException {
        String httpMethod = "POST";
        InputStream body = IOUtils.toInputStream("aBody", StandardCharsets.UTF_8);

        when(httpRequest.getRequestUri()).thenReturn("/path");
        when(httpRequest.getHeaders()).thenReturn(new BasicHeader[0]);
        when(httpRequest.getMethod()).thenReturn(httpMethod);
        when(httpRequest.getEntity()).thenReturn(EntityBuilder.create().setStream(body).build());

        Map<String, String> headers = getSignatureHeaders();
        when(ibanityHttpSignatureService.getHttpSignatureHeaders(eq(httpMethod), eq(getUrl()), eq(getRequestedHeaders()), any(InputStream.class)))
                .thenReturn(headers);

        ibanitySignatureInterceptor.process(httpRequest, entity, httpContext);

        verify(httpRequest).addHeader("digest", "value");
    }

    private Map<String, String> getSignatureHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("digest", "value");
        return headers;
    }

    private URL getUrl() throws MalformedURLException {
        return URI.create(BASE_PATH + "/path").toURL();
    }

    private Map<String, String> getRequestedHeaders() {
        return new HashMap<>();
    }
}
