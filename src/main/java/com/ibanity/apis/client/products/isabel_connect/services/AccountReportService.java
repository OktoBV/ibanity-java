package com.ibanity.apis.client.products.isabel_connect.services;

import com.ibanity.apis.client.models.IsabelCollection;
import com.ibanity.apis.client.products.isabel_connect.models.AccountReport;
import com.ibanity.apis.client.products.isabel_connect.models.read.AccountReportReadQuery;
import com.ibanity.apis.client.products.isabel_connect.models.read.AccountReportsReadQuery;
import org.apache.hc.core5.http.ClassicHttpResponse;

import java.util.function.Function;

public interface AccountReportService {
    IsabelCollection<AccountReport> list(AccountReportsReadQuery query);

    String find(AccountReportReadQuery query);

    <T> T find(AccountReportReadQuery query, Function<ClassicHttpResponse, T> func);
}
