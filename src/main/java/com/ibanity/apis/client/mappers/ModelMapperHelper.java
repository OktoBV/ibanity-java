package com.ibanity.apis.client.mappers;

import com.ibanity.apis.client.jsonapi.RequestApiModel;
import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;

import java.io.IOException;

import static com.ibanity.apis.client.http.handler.IbanityResponseHandler.IBANITY_REQUEST_ID_HEADER;
import static org.apache.hc.core5.http.io.entity.EntityUtils.consumeQuietly;

public class ModelMapperHelper {
    private static final String DEFAULT_ENCODING = "UTF-8";

    public static String getRequestId(ClassicHttpResponse httpResponse) {
        Header header = httpResponse.getFirstHeader(IBANITY_REQUEST_ID_HEADER);
        return header == null ? null : header.getValue();
    }

    public static RequestApiModel buildRequest(String resourceType, Object attributes) {
        return buildRequest(resourceType, attributes, null);
    }

    public static RequestApiModel buildRequest(String resourceType, Object attributes, Object meta) {
        return RequestApiModel.builder()
                .data(
                        RequestApiModel.RequestDataApiModel.builder()
                                .type(resourceType)
                                .meta(meta)
                                .attributes(attributes)
                                .build()
                )
                .build();
    }

    public static String readResponseContent(HttpEntity entity) throws IOException {
        try {
            return IOUtils.toString(entity.getContent(), DEFAULT_ENCODING);
        } finally {
            consumeQuietly(entity);
        }
    }
}
