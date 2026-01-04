package com.ibanity.apis.client.products.xs2a.services.impl;

import com.ibanity.apis.client.http.IbanityHttpClient;
import com.ibanity.apis.client.mappers.IbanityModelMapper;
import com.ibanity.apis.client.models.IbanityCollection;
import com.ibanity.apis.client.models.IbanityProduct;
import com.ibanity.apis.client.products.xs2a.models.FinancialInstitution;
import com.ibanity.apis.client.products.xs2a.models.read.FinancialInstitutionReadQuery;
import com.ibanity.apis.client.products.xs2a.models.read.FinancialInstitutionsReadQuery;
import com.ibanity.apis.client.products.xs2a.services.FinancialInstitutionsService;
import com.ibanity.apis.client.services.ApiUrlProvider;

import java.net.URI;

import static com.ibanity.apis.client.utils.URIHelper.buildUri;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import org.apache.hc.core5.http.ClassicHttpResponse;

public class FinancialInstitutionsServiceImpl implements FinancialInstitutionsService {

    private final ApiUrlProvider apiUrlProvider;
    private final IbanityHttpClient ibanityHttpClient;

    public FinancialInstitutionsServiceImpl(ApiUrlProvider apiUrlProvider, IbanityHttpClient ibanityHttpClient) {
        this.apiUrlProvider = apiUrlProvider;
        this.ibanityHttpClient = ibanityHttpClient;
    }

    @Override
    public IbanityCollection<FinancialInstitution> list(FinancialInstitutionsReadQuery financialInstitutionsReadQuery) {

        String customerAccessToken = financialInstitutionsReadQuery.getCustomerAccessToken();
        URI uri = isCursorBased(financialInstitutionsReadQuery) ?
                buildUriCursorBased(financialInstitutionsReadQuery, customerAccessToken) : buildUriOffsetBased(financialInstitutionsReadQuery, customerAccessToken);
        ClassicHttpResponse response = ibanityHttpClient.get(uri, financialInstitutionsReadQuery.getAdditionalHeaders(), customerAccessToken);
        return IbanityModelMapper.mapCollection(response, FinancialInstitution.class);
    }

    private boolean isCursorBased(FinancialInstitutionsReadQuery financialInstitutionsReadQuery) {
        return financialInstitutionsReadQuery.getOffsetPagingSpec() == null;
    }

    private URI buildUriOffsetBased(FinancialInstitutionsReadQuery financialInstitutionsReadQuery, String customerAccessToken) {
        return buildUri(
                removeEnd(
                        getUrl(customerAccessToken).replace(FinancialInstitution.API_URL_TAG_ID, ""),
                        "/"
                ),
                financialInstitutionsReadQuery.getOffsetPagingSpec(),
                financialInstitutionsReadQuery.getFilters());
    }

    private URI buildUriCursorBased(FinancialInstitutionsReadQuery financialInstitutionsReadQuery, String customerAccessToken) {
        return buildUri(
                removeEnd(
                        getUrl(customerAccessToken).replace(FinancialInstitution.API_URL_TAG_ID, ""),
                        "/"
                ),
                financialInstitutionsReadQuery.getPagingSpec(),
                financialInstitutionsReadQuery.getFilters());
    }

    @Override
    public FinancialInstitution find(FinancialInstitutionReadQuery financialInstitutionReadQuery) {
        String customerAccessToken = financialInstitutionReadQuery.getCustomerAccessToken();
        URI uri = buildUri(
                removeEnd(
                        getUrl(customerAccessToken)
                                .replace(FinancialInstitution.API_URL_TAG_ID, financialInstitutionReadQuery.getFinancialInstitutionId().toString()),
                        "/"));
        ClassicHttpResponse response = ibanityHttpClient.get(uri, financialInstitutionReadQuery.getAdditionalHeaders(), customerAccessToken);
        return IbanityModelMapper.mapResource(response, FinancialInstitution.class);
    }

    private String getUrl(String customerAccessToken) {
        if (isBlank(customerAccessToken)) {
            return apiUrlProvider.find(IbanityProduct.Xs2a, "financialInstitutions");
        } else {
            return apiUrlProvider.find(IbanityProduct.Xs2a, "customer", "financialInstitutions");
        }
    }
}
