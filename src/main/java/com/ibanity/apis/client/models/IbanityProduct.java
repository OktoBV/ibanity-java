package com.ibanity.apis.client.models;

public enum IbanityProduct {

    IsabelConnect("isabel-connect"),
    PontoConnect("ponto-connect"),
    Xs2a("xs2a");

    private final String path;

    IbanityProduct(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
