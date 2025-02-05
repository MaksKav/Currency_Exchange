package com.maxkavun.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/currencies/*")
public class CurrencyContentTypeFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            response.setContentType("application/x-www-form-urlencoded");
        }

        filterChain.doFilter(request, response);

    }
}

