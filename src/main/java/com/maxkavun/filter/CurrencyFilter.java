package com.maxkavun.filter;

import com.maxkavun.util.ResponseUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/currency/*")
public class CurrencyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();
            String code = pathInfo.substring(1).toUpperCase();

            if (!code.matches("^[A-Za-z]{3}$")) {
                ResponseUtil.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid currency code");
                return;
            }
        } catch (NullPointerException e) {
            ResponseUtil.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid currency code");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
