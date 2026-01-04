package com.ibanity.apis.client.products.isabel_connect.services.impl;

import com.ibanity.apis.client.http.IbanityHttpClient;
import com.ibanity.apis.client.jsonapi.isabel_connect.DataApiModel;
import com.ibanity.apis.client.mappers.IsabelModelMapper;
import com.ibanity.apis.client.models.IbanityProduct;
import com.ibanity.apis.client.models.IsabelCollection;
import com.ibanity.apis.client.products.isabel_connect.models.Transaction;
import com.ibanity.apis.client.products.isabel_connect.models.read.TransactionsReadQuery;
import com.ibanity.apis.client.products.isabel_connect.services.TransactionService;
import com.ibanity.apis.client.services.ApiUrlProvider;
import com.ibanity.apis.client.utils.StringUtils;
import org.apache.hc.core5.http.ClassicHttpResponse;

import java.util.function.Function;

import static com.ibanity.apis.client.mappers.IsabelModelMapper.toIsabelModel;
import static com.ibanity.apis.client.utils.URIHelper.buildUri;

public class TransactionServiceImpl implements TransactionService {
    private final ApiUrlProvider apiUrlProvider;
    private final IbanityHttpClient ibanityHttpClient;

    public TransactionServiceImpl(ApiUrlProvider apiUrlProvider, IbanityHttpClient ibanityHttpClient) {
        this.apiUrlProvider = apiUrlProvider;
        this.ibanityHttpClient = ibanityHttpClient;
    }

    @Override
    public IsabelCollection<Transaction> list(TransactionsReadQuery query) {
        ClassicHttpResponse response = ibanityHttpClient.get(
                buildUri(getUrl(query.getAccountId()), query.getPagingSpec()),
                query.getAdditionalHeaders(),
                query.getAccessToken());

        return IsabelModelMapper.mapCollection(response, customMappingFunction());
    }

    private String getUrl(String accountId) {
        String url = apiUrlProvider
                .find(IbanityProduct.IsabelConnect, "account", "transactions")
                .replace(Transaction.API_URL_TAG_ID, accountId);

        return StringUtils.removeEnd(url, "/");
    }

    private Function<DataApiModel, Transaction> customMappingFunction() {
        return dataApiModel -> toIsabelModel(dataApiModel, Transaction.class);
    }
}
