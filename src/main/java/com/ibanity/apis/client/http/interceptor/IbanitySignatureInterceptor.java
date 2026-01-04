package com.ibanity.apis.client.http.interceptor;

import com.ibanity.apis.client.http.service.IbanityHttpSignatureService;
import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.http.message.HttpRequestWrapper;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ibanity.apis.client.http.IbanityHttpClient.DEFAULT_ENCODING;

public class IbanitySignatureInterceptor implements HttpRequestInterceptor {

    private static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_ENCODING);
    private final IbanityHttpSignatureService httpSignatureService;
    private final String basePath;

    public IbanitySignatureInterceptor(IbanityHttpSignatureService httpSignatureService, String basePath) {
        this.httpSignatureService = httpSignatureService;
        this.basePath = basePath;
    }

    @Override
    public void process(final HttpRequest httpRequest, final EntityDetails entity, final HttpContext httpContext) throws IOException {
        try {

            InputStream payload;
            if (httpRequest instanceof BasicClassicHttpRequest original) {
                payload = original.getEntity().getContent();
            } else {
                payload = IOUtils.toInputStream("", DEFAULT_CHARSET);
            }

            httpSignatureService.getHttpSignatureHeaders(
                            httpRequest.getMethod(),
                            getUrl(httpRequest),
                            getAllHeaders(httpRequest),
                            payload)
                    .entrySet()
                    .forEach(addHeaderToRequest(httpRequest));
        } catch (Exception exception) {
            throw new IOException(exception.getMessage(), exception);
        }
    }

    private URL getUrl(HttpRequest requestWrapper) throws MalformedURLException {
        return URI.create(basePath + requestWrapper.getRequestUri()).toURL();
    }

    private Consumer<? super Map.Entry<String, String>> addHeaderToRequest(HttpRequest httpRequest) {
        return (entry) -> httpRequest.addHeader(entry.getKey(), entry.getValue());
    }

    private Map<String, String> getAllHeaders(HttpRequest requestWrapper) {
        return Stream.of(requestWrapper.getHeaders())
                .collect(Collectors.toMap(Header::getName,
                        Header::getValue));
    }

}
