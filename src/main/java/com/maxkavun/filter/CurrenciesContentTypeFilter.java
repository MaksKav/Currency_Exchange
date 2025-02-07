package com.maxkavun.filter;

import com.google.gson.Gson;
import com.maxkavun.util.ResponseUtil;
import com.maxkavun.validator.CurrencyValidator;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

@WebFilter("/currencies")
public class CurrenciesContentTypeFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String name = request.getParameter("name");
            String code = request.getParameter("code").toUpperCase();
            String sign = request.getParameter("sign");

            if (!CurrencyValidator.isValidCurrencieData(name, code, sign)) {
                ResponseUtil.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid currency data");
                return;
            }
        }

        if ("PUT".equalsIgnoreCase(request.getMethod())) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }

            Gson gson = new Gson();
            Map<String, String> requestData = gson.fromJson(stringBuilder.toString(), Map.class);

            String name = requestData.get("name");
            String code = requestData.get("code");
            String sign = requestData.get("sign");

            if (code != null) {
                code = code.toUpperCase();
            }

            if (!CurrencyValidator.isValidCurrencieData(name, code, sign)) {
                ResponseUtil.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid currency data");
                return;
            }

            request.setAttribute("name", name);
            request.setAttribute("code", code);
            request.setAttribute("sign", sign);

        }


        filterChain.doFilter(request, response);

    }
}

