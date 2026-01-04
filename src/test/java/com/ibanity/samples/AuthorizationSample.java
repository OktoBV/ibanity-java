package com.ibanity.samples;

import com.ibanity.apis.client.products.xs2a.models.*;
import com.ibanity.apis.client.products.xs2a.models.create.*;
import com.ibanity.apis.client.products.xs2a.services.AccountInformationAccessRequestAuthorizationsService;
import com.ibanity.apis.client.products.xs2a.services.AccountInformationAccessRequestsService;
import com.ibanity.apis.client.services.IbanityService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AuthorizationSample {

    private final AccountInformationAccessRequestAuthorizationsService authorizationsService;
    private final AccountInformationAccessRequestsService accountInformationAccessRequestsService;

    public AuthorizationSample(IbanityService ibanityService) {
        accountInformationAccessRequestsService = ibanityService.xs2aService().accountInformationAccessRequestsService();
        authorizationsService = ibanityService.xs2aService().accountInformationAccessRequestAuthorizationsService();
    }

    public AccountInformationAccessRequest createAiar(
            FinancialInstitution financialInstitution,
            CustomerAccessToken customerAccessToken,
            String consentReference, String redirectUrl) {
        AccountInformationAccessRequestCreationQuery accountInformationAccessRequestCreationQuery =
                AccountInformationAccessRequestCreationQuery.builder()
                        .customerAccessToken(customerAccessToken.getToken())
                        .financialInstitutionId(financialInstitution.getId())
                        .redirectUri(redirectUrl)
                        .consentReference(consentReference)
                        .allowFinancialInstitutionRedirectUri(true)
                        .customerIpAddress("1.1.1.1")
                        .allowedAccountSubtypes(Arrays.asList("checking", "savings", "securities"))
                        .skipIbanityCompletionCallback(true)
                        .state("myCustomState")
                        .metaRequestCreationQuery(MetaRequestCreationQuery.builder()
                                .authorizationPortalCreationQuery(AuthorizationPortalCreationQuery.builder()
                                        .disclaimerContent("thisIsACusomOneContent")
                                        .disclaimerTitle("thisIsACusomOneTitle")
                                        .financialInstitutionPrimaryColor("#000000")
                                        .financialInstitutionSecondaryColor("#000000")
                                        .build())
                                .build())
                        .build();

        return accountInformationAccessRequestsService.create(accountInformationAccessRequestCreationQuery);
    }

    public AccountInformationAccessRequestAuthorization create(AccountInformationAccessRequest accountInformationAccessRequest, FinancialInstitution financialInstitution, CustomerAccessToken customerAccessToken, String code) {
        Map<String, String> authorizationCode = new HashMap<>();
        authorizationCode.put("code", code);
        AccountInformationAccessRequestAuthorizationCreationQuery authorizationCreationQuery = AccountInformationAccessRequestAuthorizationCreationQuery.builder()
                .accountInformationAccessRequestId(accountInformationAccessRequest.getId())
                .customerAccessToken(customerAccessToken.getToken())
                .financialInstitutionId(financialInstitution.getId())
                .queryParameters(authorizationCode)
                .build();
        return authorizationsService.create(authorizationCreationQuery);
    }
}
