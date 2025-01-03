package com.maxkavun.servlet;

import com.google.gson.Gson;
import com.maxkavun.dto.ExchangeRateDto;
import com.maxkavun.exception.BusinessException;
import com.maxkavun.exception.CurrencyNotFoundException;
import com.maxkavun.exception.ExchangeRateAlreadyExistsException;
import com.maxkavun.factory.ExchangeRateServiceFactory;
import com.maxkavun.service.ExchangeRateService;
import com.maxkavun.util.ResponceUtil;
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
import java.util.Optional;

@WebServlet("/exchangeRates/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateServiceFactory.createExchangeRateService();
    private final Gson gson = new Gson();
    private static final Logger log = LoggerFactory.getLogger(ExchangeRateServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String path = request.getPathInfo();
            if (path == null || path.equals("/")) {
                List<ExchangeRateDto> exchangeRateDtoList = exchangeRateService.getAllExchangeRates();
                log.info("Successfully retrieved all exchangeRates in doGet");
                ResponceUtil.sendResponse(response, HttpServletResponse.SC_OK, gson.toJson(exchangeRateDtoList));
            } else {
                String exchangeRateCode = path.substring(1);
                Optional<ExchangeRateDto> exchangeRateDto = exchangeRateService.getExchangeRateByCode(exchangeRateCode);
                if (exchangeRateDto.isPresent()) {
                    log.info("Successfully retrieved exchange rate in doGet for exchangeRateCode: {}", exchangeRateCode);
                    ResponceUtil.sendResponse(response, HttpServletResponse.SC_OK, gson.toJson(exchangeRateDto.get()));
                } else {
                    log.warn("No exchange rate found for exchangeRateCode: {}", exchangeRateCode);
                    ResponceUtil.sendResponse(response, HttpServletResponse.SC_NOT_FOUND, "No exchange rate found for exchangeRateCode: " + exchangeRateCode);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            ResponceUtil.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, gson.toJson(e));
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String baseCurrencyCode = request.getParameter("baseCurrencyCode");
            String targetCurrencyCode = request.getParameter("targetCurrencyCode");
            BigDecimal rate = new BigDecimal(request.getParameter("rate"));

            log.info("Attempting to add new exchange rate: baseCurrencyCode={}, targetCurrencyCode={}, rate={}",
                    baseCurrencyCode, targetCurrencyCode, rate);

            ExchangeRateDto exchangeRateDto = exchangeRateService.addNewExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);

            if (exchangeRateDto != null) {
                ResponceUtil.sendResponse(response, HttpServletResponse.SC_CREATED, gson.toJson(exchangeRateDto));
                log.info("Successfully added new exchange rate: {}", gson.toJson(exchangeRateDto));
            }

        } catch (CurrencyNotFoundException e) {
            log.warn("Failed to add exchange rate: {}", e.getMessage());
            ResponceUtil.sendResponse(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (ExchangeRateAlreadyExistsException e) {
            log.warn("Failed to add exchange rate: {}", e.getMessage());
            ResponceUtil.sendResponse(response, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (IOException e) {
            log.error("An error occurred while adding a new exchange rate: {}", e.getMessage());
            ResponceUtil.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            String path = request.getPathInfo().substring(1).toUpperCase();
            BigDecimal rate = new BigDecimal(request.getParameter("rate"));

            ExchangeRateDto exchangeRateDto = exchangeRateService.updateExchangeRate(path , rate);
            log.info("Successfully updated exchange rate: {}", exchangeRateDto);
            ResponceUtil.sendResponse(response, HttpServletResponse.SC_OK, gson.toJson(exchangeRateDto));

        } catch (IOException e) {
            log.error("Failed to update exchange rate: {}", e.getMessage());
            ResponceUtil.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }



}
