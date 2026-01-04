package com.ibanity.apis.client.helpers;

import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

public class IbanityTestHelper {

    public static final ProtocolVersion HTTP = new ProtocolVersion("HTTP", 0, 0);

    public static ClassicHttpResponse loadHttpResponse(String filePath) throws IOException {
        String jsonResponse = loadFile(filePath);
        ClassicHttpResponse httpResponse = new BasicClassicHttpResponse(200);
        httpResponse.setEntity(new StringEntity(jsonResponse));
        return httpResponse;
    }

    public static ClassicHttpResponse createHttpResponse(String expected) throws UnsupportedEncodingException {
        return createHttpResponse(expected, new Header[]{});
    }

    public static ClassicHttpResponse createHttpResponse(String expected, Header... headers) {
        ClassicHttpResponse postResponse = new BasicClassicHttpResponse(200);
        postResponse.setEntity(new StringEntity(expected));
        postResponse.setHeaders(headers);
        return postResponse;
    }

    public static String loadFile(String filePath) throws IOException {
        return IOUtils.toString(
                requireNonNull(IbanityTestHelper.class.getClassLoader().getResourceAsStream(filePath)), UTF_8);
    }
}
