package com.yeel.drc.model.dummy;

public class AddNewCurrencyList {
    private String currency_id;
    private String currency_name;
    private String currency_code;

    public AddNewCurrencyList(String currency_id, String currency_name, String currency_code) {
        this.currency_id = currency_id;
        this.currency_name = currency_name;
        this.currency_code = currency_code;
    }

    public String getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(String currency_id) {
        this.currency_id = currency_id;
    }

    public String getCurrency_name() {
        return currency_name;
    }

    public void setCurrency_name(String currency_name) {
        this.currency_name = currency_name;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }
}
