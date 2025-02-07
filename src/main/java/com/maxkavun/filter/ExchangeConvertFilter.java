package com.maxkavun.filter;

import com.maxkavun.validator.RateAmountValidator;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.maxkavun.util.ResponceUtil;

import java.io.IOException;


@WebFilter("/exchange")
public class ExchangeConvertFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        if (!request.getMethod().equalsIgnoreCase("GET")) {
            ResponceUtil.sendErrorResponse(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method Not Allowed");
            return;
        }

        String fromCurrency = request.getParameter("from");
        String toCurrency = request.getParameter("to");
        String amountParam = request.getParameter("amount");

        if (fromCurrency == null || toCurrency == null || amountParam == null) {
            ResponceUtil.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters: 'from', 'to', 'amount'");
            return;
        }


        if (fromCurrency.length() != 3 || toCurrency.length() != 3) {
            ResponceUtil.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Wrong parameters: 'from', 'to'");
            return;
        }


        if (!RateAmountValidator.isValidRate(amountParam)) {
            ResponceUtil.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Amount is not valid");
            return;
        }

        chain.doFilter(request, response);
    }
}
