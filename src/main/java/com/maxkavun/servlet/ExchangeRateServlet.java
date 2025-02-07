package com.maxkavun.servlet;

import com.google.gson.Gson;
import com.maxkavun.dto.ExchangeRateDto;
import com.maxkavun.factory.ExchangeRateServiceFactory;
import com.maxkavun.service.ExchangeRateService;
import com.maxkavun.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateServiceFactory.createExchangeRateService();
    private final Gson gson = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String path = request.getPathInfo();
            String exchangeRateCode = path.substring(1);
            Optional<ExchangeRateDto> exchangeRateDto = exchangeRateService.getExchangeRateByCode(exchangeRateCode);
            if (exchangeRateDto.isPresent()) {
                LOGGER.info("Successfully retrieved exchange rate in doGet for exchangeRateCode: {}", exchangeRateCode);
                ResponseUtil.sendResponse(response, HttpServletResponse.SC_OK, gson.toJson(exchangeRateDto.get()));
            } else {
                LOGGER.warn("No exchange rate found for exchangeRateCode: {}", exchangeRateCode);
                ResponseUtil.sendResponse(response, HttpServletResponse.SC_NOT_FOUND, "No exchange rate found for exchangeRateCode: " + exchangeRateCode);
            }
        } catch (NullPointerException e) {
            LOGGER.error(e.getMessage());
            ResponseUtil.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Exchange rate code is null");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            ResponseUtil.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ooopps");
        }
    }


    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String path = request.getPathInfo().substring(1).toUpperCase();
            String body = (String) request.getAttribute("requestBody");

            Map<String, String> parameters = parseFormData(body);

            String rateStr = parameters.get("rate");
            BigDecimal rate = new BigDecimal(rateStr);

            ExchangeRateDto exchangeRateDto = exchangeRateService.updateExchangeRate(path, rate);
            LOGGER.info("Successfully updated exchange rate: {}", exchangeRateDto);
            ResponseUtil.sendResponse(response, HttpServletResponse.SC_OK, gson.toJson(exchangeRateDto));

        } catch (IOException e) {
            LOGGER.error("Failed to update exchange rate: {}", e.getMessage());
            ResponseUtil.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update exchange rate");
        }
    }


    private static Map<String, String> parseFormData(String body) {
        return Arrays.stream(body.split("&"))
                .map(param -> param.split("=", 2))
                .collect(Collectors.toMap(p -> URLDecoder.decode(p[0], StandardCharsets.UTF_8),
                        p -> URLDecoder.decode(p.length > 1 ? p[1] : "", StandardCharsets.UTF_8)));
    }
}
