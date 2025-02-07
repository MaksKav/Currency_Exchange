package com.maxkavun.servlet;

import com.google.gson.Gson;
import com.maxkavun.dto.ExchangeConvertDto;
import com.maxkavun.dto.ExchangeConvertDtoCustom;
import com.maxkavun.dto.ExchangeErrorResponse;
import com.maxkavun.exception.BusinessException;
import com.maxkavun.exception.CurrencyNotFoundException;
import com.maxkavun.exception.DataAccessException;
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

@WebServlet("/exchange")
public class ExchangeConvertServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateServiceFactory.createExchangeRateService();
    private final Gson gson = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeConvertServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String fromCurrency = request.getParameter("from");
            String toCurrency = request.getParameter("to");
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));


            ExchangeConvertDto resultExchange = exchangeRateService.calculateExchange(fromCurrency, toCurrency, amount);
            ResponseUtil.sendResponse(response, HttpServletResponse.SC_OK, gson.toJson(new ExchangeConvertDtoCustom(resultExchange)));

        }catch (NumberFormatException e ){
            LOGGER.error(e.getMessage());
            String errorMessage =  gson.toJson(new ExchangeErrorResponse(e.getMessage()));
            ResponseUtil.sendResponse(response,HttpServletResponse.SC_BAD_REQUEST , errorMessage);
        } catch (CurrencyNotFoundException e) {
            LOGGER.error(e.getMessage());
            String errorMessage =  gson.toJson(new ExchangeErrorResponse(e.getMessage()));
            ResponseUtil.sendResponse(response,HttpServletResponse.SC_NOT_FOUND , errorMessage);
        } catch (IOException | BusinessException  |DataAccessException e) {
            LOGGER.error(e.getMessage());
            String errorMessage =  gson.toJson(new ExchangeErrorResponse(e.getMessage()));
            ResponseUtil.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
        }

    }
}
