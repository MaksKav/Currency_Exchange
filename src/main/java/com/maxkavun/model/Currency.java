package com.maxkavun.model;

import java.util.Objects;

public class Currency {

    private Integer id;
    private String fullName;
    private String code;
    private String sign;

    public Currency(Integer id, String fullName, String code, String sign) {
        this.id = id;
        this.fullName = fullName;
        this.code = code;
        this.sign = sign;
    }

    public Currency(String fullName, String code, String sign) {
        this.fullName = fullName;
        this.code = code;
        this.sign = sign;
    }

    public Currency() {
    }

    @Override
    public String toString() {
        return "Currency{" +
               "id=" + id +
               ", fullName='" + fullName + '\'' +
               ", code='" + code + '\'' +
               ", sign='" + sign + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(id, currency.id) && Objects.equals(code, currency.code) && Objects.equals(fullName, currency.fullName) && Objects.equals(sign, currency.sign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, fullName, sign);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
