package com.maxkavun.servlet;

import com.google.gson.Gson;
import com.maxkavun.dto.ExchangeRateDto;
import com.maxkavun.exception.CurrencyNotFoundException;
import com.maxkavun.exception.ExchangeRateAlreadyExistsException;
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
import java.util.List;


@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeRatesService = ExchangeRateServiceFactory.createExchangeRateService();
    private final Gson gson = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRatesServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<ExchangeRateDto> exchangeRateDtoList = exchangeRatesService.getAllExchangeRates();
            LOGGER.info("Successfully retrieved all exchangeRates in doGet");
            ResponseUtil.sendResponse(response, HttpServletResponse.SC_OK, gson.toJson(exchangeRateDtoList));
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            ResponseUtil.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ooopps");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String baseCurrencyCode = request.getParameter("baseCurrencyCode");
            String targetCurrencyCode = request.getParameter("targetCurrencyCode");
            String rateStr = request.getParameter("rate").trim();
            BigDecimal rate = new BigDecimal(rateStr);

            ExchangeRateDto exchangeRateDto = exchangeRatesService.addNewExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);

            if (exchangeRateDto != null) {
                ResponseUtil.sendResponse(response, HttpServletResponse.SC_CREATED, gson.toJson(exchangeRateDto));
                LOGGER.info("Successfully added new exchange rate: {}", gson.toJson(exchangeRateDto));
            }

        } catch (CurrencyNotFoundException e) {
            ResponseUtil.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Currency not found in database");
        } catch (ExchangeRateAlreadyExistsException e) {
            ResponseUtil.sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, "This exchange rate is exist");
        } catch (IOException e) {
            LOGGER.error("An error occurred while adding a new exchange rate: {}", e.getMessage());
            ResponseUtil.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ooops an error occurred while adding a new exchange rate ");
        }
    }

}
