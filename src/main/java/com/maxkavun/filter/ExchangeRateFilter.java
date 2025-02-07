package com.maxkavun.filter;

import com.maxkavun.util.ResponceUtil;
import com.maxkavun.validator.RateAmountValidator;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@WebFilter("/exchangeRate/*")
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

            path = path.substring(1).toUpperCase();
            if (path.length() != codeLength || !path.matches("[A-Z]+")) {
                ResponceUtil.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "The currencies code are incorrect. It must be exactly 6 uppercase letters.");
                return;
            }
            filterChain.doFilter(request, response);
        }


        if ("PATCH".equalsIgnoreCase(request.getMethod())) {
            String path = request.getPathInfo();
            if (path == null || path.equals("/")) {
                ResponceUtil.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "The currencies code are incorrect " + path);
                return;
            }

            int codeLength = 6;
            path = path.substring(1).toUpperCase();
            if (path.length() != codeLength || !path.matches("[A-Z]+")) {
                ResponceUtil.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "The currencies code are incorrect " + path);
                return;
            }

            BufferedReader reader = request.getReader();
            String body = reader.lines().collect(Collectors.joining());
            request.setAttribute("requestBody", body);

            Map<String, String> parameters = parseFormData(body);

            String rateStr = parameters.get("rate");

            if (!RateAmountValidator.isValidRate(rateStr)) {
                ResponceUtil.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "The rate is invalid");
                return;
            }

            filterChain.doFilter(request, response);
        }
    }

    private static Map<String, String> parseFormData(String body) {
        return Arrays.stream(body.split("&"))
                .map(param -> param.split("=", 2))
                .collect(Collectors.toMap(p -> URLDecoder.decode(p[0], StandardCharsets.UTF_8),
                        p -> URLDecoder.decode(p.length > 1 ? p[1] : "", StandardCharsets.UTF_8)));
    }
}