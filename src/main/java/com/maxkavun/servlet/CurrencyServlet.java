package com.maxkavun.servlet;

import com.google.gson.Gson;
import com.maxkavun.dto.CurrencyDto;
import com.maxkavun.exception.BusinessException;
import com.maxkavun.exception.ModelToDtoConversionException;
import com.maxkavun.exception.ValidationException;
import com.maxkavun.service.CurrencyService;
import com.maxkavun.util.ResponceUtil;
import com.maxkavun.validator.CurrencyValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


@WebServlet("/currencies/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService  = new CurrencyService();
    private final Gson gson = new Gson();
    private static final Logger log = LoggerFactory.getLogger(CurrencyServlet.class);


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                List<CurrencyDto> currencies = currencyService.getAllCurrencies();
                ResponceUtil.sendResponse(response , HttpServletResponse.SC_OK , gson.toJson(currencies));
                log.info("Successfully retrieved all currencies in doGet.");
            } else {
                String code = pathInfo.substring(1);
                code = code.toUpperCase();
                Optional<CurrencyDto> currencyDto = currencyService.getOptionalCurrencyByCode(code);

                if (currencyDto.isPresent() ) {
                    ResponceUtil.sendResponse(response , HttpServletResponse.SC_OK , gson.toJson(currencyDto));
                    log.info("Successfully retrieved currencyOptional by code: {}" , code);
                } else {
                    ResponceUtil.sendResponse(response , HttpServletResponse.SC_NOT_FOUND , "Not found currency");
                    log.warn("Currency not found: {}", code);
                }
            }
        } catch (IOException e) {
            ResponceUtil.sendResponse(response , HttpServletResponse.SC_INTERNAL_SERVER_ERROR ,e.getMessage() );
            log.error("Error while retrieving currencies.", e);
        } catch (ModelToDtoConversionException e) {
            ResponceUtil.sendResponse(response , HttpServletResponse.SC_INTERNAL_SERVER_ERROR ,e.getMessage() );
            log.error("Error while retrieving currencies in ModelToDtoConversation." , e );
        } catch (BusinessException e) {
            ResponceUtil.sendResponse(response , HttpServletResponse.SC_BAD_REQUEST ,e.getMessage() );
            log.error("Error while retrieving currencies in BusinessException." , e );
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            String name = request.getParameter("fullName");
            String code = request.getParameter("code").toUpperCase();
            String sign = request.getParameter("sign");

            if (!CurrencyValidator.validateCurrencyData(name , code, sign)) {
                ResponceUtil.sendResponse(response , HttpServletResponse.SC_BAD_REQUEST , "Invalid currency data");
                log.warn("The required form field is missing in Currency POST request.");
                return;
            }

            if (currencyService.getOptionalCurrencyByCode(code).isPresent()) {
                ResponceUtil.sendResponse(response , HttpServletResponse.SC_CONFLICT , "Currency already exists");
                log.warn("Currency with this code: {}  already exists" , code);
                return;
            }

            CurrencyDto currencyDto = new CurrencyDto(code, name, sign);
            currencyService.addCurrency(currencyDto);
            ResponceUtil.sendResponse(response , HttpServletResponse.SC_CREATED , gson.toJson(currencyDto));
            log.info("Successfully created currency: {}", currencyDto.getCode());
        }catch (Exception e) {
            ResponceUtil.sendResponse(response , HttpServletResponse.SC_INTERNAL_SERVER_ERROR ,e.getMessage());
            log.error("Error while creating currency from POST ", e);
        }
    }


    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            CurrencyDto currencyDto = gson.fromJson(request.getReader() , CurrencyDto.class);

            if (!CurrencyValidator.validateCurrencyData(currencyDto.getFullName(), currencyDto.getCode(), currencyDto.getSign())) {
                ResponceUtil.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid currency data: one or more fields are incorrect.");
                log.warn("Validation failed for currency: {}", gson.toJson(currencyDto));
                return;
            }

            currencyService.updateCurrency(currencyDto);
            ResponceUtil.sendResponse(response , HttpServletResponse.SC_OK , gson.toJson(currencyDto));
            log.info("Successfully updated currency: {}", currencyDto.getCode());
        } catch (ValidationException e) {
            ResponceUtil.sendResponse(response , HttpServletResponse.SC_BAD_REQUEST , e.getMessage());
            log.warn("Validation error: {}", e.getMessage());
        } catch (BusinessException e) {
            ResponceUtil.sendResponse(response , HttpServletResponse.SC_NOT_FOUND , e.getMessage());
            log.warn("Business logic error: {}", e.getMessage());
        } catch (Exception e) {
            ResponceUtil.sendResponse(response , HttpServletResponse.SC_INTERNAL_SERVER_ERROR ,e.getMessage() );
            log.error("Error while updating currency", e);
        }
    }



}
