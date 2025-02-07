package com.maxkavun.filter;

import com.maxkavun.util.ResponceUtil;
import com.maxkavun.validator.ExchangeCurrenciesValidator;
import com.maxkavun.validator.RateAmountValidator;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@WebFilter("/exchangeRates")
public class ExchangeRatesFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest , ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setCharacterEncoding("UTF-8");

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            response.setContentType("application/json");
            filterChain.doFilter(request, response);
        }

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            response.setContentType("application/json");
            try {
                String baseCurrencyCode = request.getParameter("baseCurrencyCode").toUpperCase().trim();
                String targetCurrencyCode = request.getParameter("targetCurrencyCode").toUpperCase().trim();
                String rateStr = request.getParameter("rate");

                if (!ExchangeCurrenciesValidator.isCurrenciesCodeValid(baseCurrencyCode , targetCurrencyCode)){
                    ResponceUtil.sendErrorResponse(response , HttpServletResponse.SC_BAD_REQUEST , "Currencies are invalid");
                    return;
                }

                if (!RateAmountValidator.isValidRate(rateStr)) {
                    ResponceUtil.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "The rate is invalid");
                    return;
                }
            } catch (NumberFormatException | NullPointerException e) {
                ResponceUtil.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Rate must be a valid number");
                return;
            }
            filterChain.doFilter(request, response);
        }

    }

}
