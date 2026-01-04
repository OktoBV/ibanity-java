package com.ibanity.apis.client.products.ponto_connect.services.impl;

import com.ibanity.apis.client.http.IbanityHttpClient;
import com.ibanity.apis.client.models.IbanityProduct;
import com.ibanity.apis.client.products.ponto_connect.models.OrganizationUsage;
import com.ibanity.apis.client.products.ponto_connect.models.read.OrganizationUsageReadQuery;
import com.ibanity.apis.client.products.ponto_connect.services.UsageService;
import com.ibanity.apis.client.services.ApiUrlProvider;
import com.ibanity.apis.client.utils.IbanityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;
import org.apache.hc.core5.http.ClassicHttpResponse;
public class UsageServiceImpl implements UsageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsageServiceImpl.class);

    private final ApiUrlProvider apiUrlProvider;
    private final IbanityHttpClient ibanityHttpClient;

    public UsageServiceImpl(ApiUrlProvider apiUrlProvider, IbanityHttpClient ibanityHttpClient) {
        this.apiUrlProvider = apiUrlProvider;
        this.ibanityHttpClient = ibanityHttpClient;
    }

    @Override
    public OrganizationUsage getOrganizationUsage(OrganizationUsageReadQuery readQuery) {
        try {
            String url = getUrl(readQuery.getOrganizationId(), readQuery.getMonth());
            ClassicHttpResponse response = ibanityHttpClient.get(URI.create(url), readQuery.getAdditionalHeaders(), readQuery.getAccessToken());
            JsonNode dataApiModel = IbanityUtils.jsonMapper().readTree(response.getEntity().getContent());
            return map(dataApiModel);
        } catch (IOException e) {
            LOGGER.error("OrganizationUsage response invalid", e);
            throw new RuntimeException("The response could not be parsed.", e);
        }
    }

    private OrganizationUsage map(JsonNode dataApiModel) {
        return OrganizationUsage.builder()
                .id(dataApiModel.get("data").get("id").asString())
                .paymentCount(new BigDecimal(dataApiModel.get("data").get("attributes").get("paymentCount").toString()))
                .accountCount(new BigDecimal(dataApiModel.get("data").get("attributes").get("accountCount").toString()))
                .paymentAccountCount(new BigDecimal(dataApiModel.get("data").get("attributes").get("paymentAccountCount").toString()))
                .bulkPaymentCount(new BigDecimal(dataApiModel.get("data").get("attributes").get("bulkPaymentCount").toString()))
                .build();
    }

    private String getUrl(UUID organizationId, String month) {
        return apiUrlProvider.find(IbanityProduct.PontoConnect, "organizations", "usage")
                .replace(OrganizationUsage.API_URL_TAG_ORGANIZATION_ID, organizationId.toString())
                .replace(OrganizationUsage.API_URL_TAG_ID, month);
    }
}
