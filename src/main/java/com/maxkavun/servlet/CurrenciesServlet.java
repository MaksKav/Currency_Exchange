package com.maxkavun.servlet;
import com.google.gson.Gson;
import com.maxkavun.dto.CurrencyDto;
import com.maxkavun.exception.BusinessException;
import com.maxkavun.exception.ModelToDtoConversionException;
import com.maxkavun.exception.ValidationException;
import com.maxkavun.service.CurrencyService;
import com.maxkavun.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = new CurrencyService();
    private final Gson gson = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrenciesServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                List<CurrencyDto> currencies = currencyService.getAllCurrencies();
                ResponseUtil.sendResponse(response, HttpServletResponse.SC_OK, gson.toJson(currencies));
                LOGGER.info("Successfully retrieved all currencies in doGet.");
            }
        } catch (IOException e) {
            ResponseUtil.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            LOGGER.error("Error while retrieving currencies.", e);
        } catch (ModelToDtoConversionException e) {
            ResponseUtil.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            LOGGER.error("Error while retrieving currencies in ModelToDtoConversation.", e);
        } catch (BusinessException e) {
            ResponseUtil.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            LOGGER.error("Error while retrieving currencies in BusinessException.", e);
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String name = request.getParameter("name");
            String code = request.getParameter("code").toUpperCase();
            String sign = request.getParameter("sign");

            if (currencyService.getOptionalCurrencyByCode(code).isPresent()) {
                ResponseUtil.sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, "Currency already exists");
                LOGGER.warn("Currency with this code: {}  already exists", code);
                return;
            }

            CurrencyDto currencyDto = new CurrencyDto(name, code, sign);
            currencyService.addCurrency(currencyDto);
            ResponseUtil.sendResponse(response, HttpServletResponse.SC_CREATED, gson.toJson(currencyDto));
            LOGGER.info("Successfully created currency: {}", currencyDto.getCode());
        } catch (Exception e) {
            ResponseUtil.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ooopss");
            LOGGER.error("Error while creating currency from POST ", e);
        }
    }


    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            String name = (String) request.getAttribute("name");
            String code = (String) request.getAttribute("code");
            String sign = (String) request.getAttribute("sign");

            CurrencyDto currencyDto = new CurrencyDto(name, code, sign);

            currencyService.updateCurrency(currencyDto);
            ResponseUtil.sendResponse(response, HttpServletResponse.SC_OK, gson.toJson(currencyDto));
            LOGGER.info("Successfully updated currency: {}", currencyDto.getCode());
        } catch (ValidationException e) {
            ResponseUtil.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            LOGGER.warn("Validation error: {}", e.getMessage());
        } catch (BusinessException e) {
            ResponseUtil.sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            LOGGER.warn("Business logic error: {}", e.getMessage());
        } catch (Exception e) {
            ResponseUtil.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            LOGGER.error("Error while updating currency", e);
        }
    }


}
