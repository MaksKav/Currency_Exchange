package com.maxkavun.dto;

import java.util.Objects;

public class CurrencyDto {
    private int id;
    private String code;
    private String fullName;
    private String sign;

    public CurrencyDto(int id, String code, String fullName, String sign) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public CurrencyDto(String code, String fullName, String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public CurrencyDto() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyDto that = (CurrencyDto) o;
        return id == that.id && Objects.equals(code, that.code) && Objects.equals(fullName, that.fullName) && Objects.equals(sign, that.sign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, fullName, sign);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @Override
    public String toString() {
        return "CurrencyDto{" +
               "id=" + id +
               ", code='" + code + '\'' +
               ", fullName='" + fullName + '\'' +
               ", sign='" + sign + '\'' +
               '}';
    }
}
