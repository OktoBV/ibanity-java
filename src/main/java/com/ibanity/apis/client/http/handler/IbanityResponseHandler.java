package com.ibanity.apis.client.http.handler;

import com.ibanity.apis.client.exceptions.IbanityClientException;
import com.ibanity.apis.client.exceptions.IbanityServerException;
import com.ibanity.apis.client.jsonapi.ErrorResourceApiModel;
import com.ibanity.apis.client.jsonapi.IbanityErrorApiModel;
import com.ibanity.apis.client.jsonapi.OAuth2ErrorResourceApiModel;
import com.ibanity.apis.client.mappers.IbanityErrorMapper;
import com.ibanity.apis.client.models.IbanityError;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.ibanity.apis.client.mappers.ModelMapperHelper.readResponseContent;
import static com.ibanity.apis.client.utils.IbanityUtils.jsonMapper;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class IbanityResponseHandler implements HttpClientResponseHandler<ClassicHttpResponse> {

    private static final int CLIENT_ERROR = 400;
    private static final int SERVER_ERROR = 500;
    public static final String IBANITY_REQUEST_ID_HEADER = "ibanity-request-id";

    @Override
    public ClassicHttpResponse handleResponse(ClassicHttpResponse httpResponse) {
        int statusCode = httpResponse.getCode();
        if (statusCode >= SERVER_ERROR) {
            throw new IbanityServerException(parseErrors(httpResponse), statusCode, getIbanityRequestId(httpResponse));
        } else if (statusCode >= CLIENT_ERROR) {
            throw new IbanityClientException(parseErrors(httpResponse), statusCode, getIbanityRequestId(httpResponse));
        }

        return httpResponse;
    }

    private String getIbanityRequestId(ClassicHttpResponse httpResponse) {
        Header firstHeader = httpResponse.getFirstHeader(IBANITY_REQUEST_ID_HEADER);
        return firstHeader != null ? firstHeader.getValue() : null;
    }

    private List<IbanityError> parseErrors(ClassicHttpResponse httpResponse) {
        try {
            String payload = readResponseContent(httpResponse.getEntity());
            List<IbanityErrorApiModel> errors = jsonMapper().readValue(payload, ErrorResourceApiModel.class).getErrors();
            if (!errors.isEmpty()) {
                return errors.stream()
                        .map(IbanityErrorMapper::map)
                        .collect(Collectors.toList());
            }

            OAuth2ErrorResourceApiModel oAuth2ErrorResourceApiModel = jsonMapper().readValue(payload, OAuth2ErrorResourceApiModel.class);
            if (isNotBlank(oAuth2ErrorResourceApiModel.getError())) {
                return Collections.singletonList(IbanityErrorMapper.map(oAuth2ErrorResourceApiModel));
            }

            return emptyList();
        } catch (Exception exception) {
            throw new RuntimeException("Invalid payload", exception);
        }
    }
}
