package com.maxkavun.filter;

import com.google.gson.Gson;
import com.maxkavun.dto.ExchangeConvertErrorResponse;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.maxkavun.util.ResponceUtil;
import java.io.IOException;
import java.math.BigDecimal;

@WebFilter("/exchange")
public class ExchangeConvertFilter implements Filter {
    private final Gson gson = new Gson();

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        if (!request.getMethod().equalsIgnoreCase("GET")) {
            String errorJson = gson.toJson(new ExchangeConvertErrorResponse("Method Not Allowed"));
            ResponceUtil.sendResponse(response , HttpServletResponse.SC_METHOD_NOT_ALLOWED, errorJson);
            return;
        }


        String fromCurrency = request.getParameter("from");
        String toCurrency = request.getParameter("to");
        String amountParam = request.getParameter("amount");

        if (fromCurrency == null || toCurrency == null || amountParam == null) {
            String errorJson = gson.toJson(new ExchangeConvertErrorResponse("Missing required parameters: 'from', 'to', 'amount'"));
            ResponceUtil.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, errorJson);
            return;
        }


        if (fromCurrency.length()!=3 || toCurrency.length()!=3 ) {
            String errorJson = gson.toJson(new ExchangeConvertErrorResponse("Wrong parameters: 'from', 'to'"));
            ResponceUtil.sendResponse(response , HttpServletResponse.SC_BAD_REQUEST, errorJson);
            return;
        }


        try {
            new BigDecimal(amountParam);
        } catch (NumberFormatException e) {
            String errorJson = gson.toJson(new ExchangeConvertErrorResponse("Amount must be a valid number"));
            ResponceUtil.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, errorJson);
            return;
        }


        chain.doFilter(request, response);
    }
}
