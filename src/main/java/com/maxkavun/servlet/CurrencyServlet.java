package com.maxkavun.servlet;

import com.google.gson.Gson;
import com.maxkavun.dto.CurrencyDto;
import com.maxkavun.exception.BusinessException;
import com.maxkavun.exception.ModelToDtoConversionException;
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
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = new CurrencyService();
    private final Gson gson = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrenciesServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();

            String code = pathInfo.substring(1).toUpperCase();
            Optional<CurrencyDto> currencyDto = currencyService.getOptionalCurrencyByCode(code);

            if (currencyDto.isPresent()) {
                ResponseUtil.sendResponse(response, HttpServletResponse.SC_OK, gson.toJson(currencyDto));
                LOGGER.info("Successfully retrieved currencyOptional by code: {}", code);
            } else {
                ResponseUtil.sendResponse(response, HttpServletResponse.SC_NOT_FOUND, "Not found currency");
                LOGGER.warn("Currency not found: {}", code);
            }

        } catch (ModelToDtoConversionException e) {
            ResponseUtil.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            LOGGER.error("Error while retrieving currencies in ModelToDtoConversation.", e);
        } catch (BusinessException e) {
            ResponseUtil.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            LOGGER.error("Error while retrieving currencies in BusinessException.", e);
        } catch (IOException e) {
            ResponseUtil.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            LOGGER.error("Error while retrieving currencies.", e);
        }
    }
}
