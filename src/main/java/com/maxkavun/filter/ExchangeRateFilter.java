package com.maxkavun.filter;

import com.maxkavun.util.ResponceUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebFilter("/exchangeRates/*")
public class ExchangeRateFilter implements Filter {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setContentType("application/x-www-form-urlencoded");
        response.setCharacterEncoding("UTF-8");


        if ("GET".equalsIgnoreCase(request.getMethod())) {
            response.setContentType("application/json");
            String path = request.getPathInfo();
            int codeLength = 6;
            if (path == null || path.equals("/")) {
                filterChain.doFilter(request, response);
                return;
            }

            path = path.substring(1).toUpperCase();

            if (path.length() != codeLength || !path.matches("[A-Z]+")) {
                ResponceUtil.sendResponse(response , HttpServletResponse.SC_BAD_REQUEST ,"The currencies code are incorrect. It must be exactly 6 uppercase letters." );
            } else {
                filterChain.doFilter(request, response);
            }
        }


        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String baseCurrencyCode = request.getParameter("baseCurrencyCode");
            String targetCurrencyCode = request.getParameter("targetCurrencyCode");
            BigDecimal rate = new BigDecimal(request.getParameter("rate"));
            int codeLength = 3;
            if (baseCurrencyCode.length() != codeLength  || targetCurrencyCode.length() != codeLength ||
                !baseCurrencyCode.matches("[A-Z]+") || !targetCurrencyCode.matches("[A-Z]+") ) {
                ResponceUtil.sendResponse(response , HttpServletResponse.SC_BAD_REQUEST , "Values in form are incorrect");
            } else {
                filterChain.doFilter(request, response);
            }
        }


        if("PATCH".equalsIgnoreCase(request.getMethod())) {
            String path = request.getPathInfo();

            if (path == null || path.equals("/")) {
                ResponceUtil.sendResponse(response , HttpServletResponse.SC_BAD_REQUEST , "The currencies code are incorrect " + path);
                return;
            }

            int codeLength = 6;

            path = path.substring(1).toUpperCase();
            if (path.length() != codeLength || !path.matches("[A-Z]+")) {
                ResponceUtil.sendResponse(response , HttpServletResponse.SC_BAD_REQUEST , "The currencies code are incorrect " + path);
            }

            BigDecimal rate = new BigDecimal(request.getParameter("rate"));
            if (rate.compareTo(BigDecimal.ZERO) < 0 || rate == null ) {
                ResponceUtil.sendResponse(response , HttpServletResponse.SC_BAD_REQUEST , "Rate in form is incorrect " + rate);
            }else {
                filterChain.doFilter(request, response);
            }
        }



    }
}