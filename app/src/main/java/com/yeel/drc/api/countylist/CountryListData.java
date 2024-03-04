package com.yeel.drc.api.countylist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryListData {
    @SerializedName("country_name") @Expose private String countryName;
    @SerializedName("flag") @Expose private String flag;
    @SerializedName("country_code") @Expose private String countryMobileNumberCode;
    @SerializedName("no_of_digits") @Expose private String no_of_digits;
    @SerializedName("country_character_code") @Expose private String countrCharacterCode;
    @SerializedName("id") @Expose private String id;
    @SerializedName("format") @Expose private String format;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getNo_of_digits() {
        return no_of_digits;
    }

    public void setNo_of_digits(String no_of_digits) {
        this.no_of_digits = no_of_digits;
    }

    public String getCountrCharacterCode() {
        return countrCharacterCode;
    }

    public void setCountrCharacterCode(String countrCharacterCode) {
        this.countrCharacterCode = countrCharacterCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCountryMobileNumberCode() {
        return countryMobileNumberCode;
    }

    public void setCountryMobileNumberCode(String countryMobileNumberCode) {
        this.countryMobileNumberCode = countryMobileNumberCode;
    }
}
