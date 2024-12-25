package com.maxkavun.servlet;

import com.google.gson.Gson;
import com.maxkavun.dto.ExchangeRateDto;
import com.maxkavun.factory.ExchangeRateServiceFactory;
import com.maxkavun.service.ExchangeRateService;
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

@WebServlet("/exchangeRates/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService  = ExchangeRateServiceFactory.createExchangeRateService();
    private final Gson gson = new Gson();
    private static final Logger log = LoggerFactory.getLogger(ExchangeRateServlet.class);


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try{
            String path = request.getPathInfo();
            if(path == null || path.equals("/")){
                List<ExchangeRateDto> exchangeRateDtoList = exchangeRateService.getAllExchangeRates();
                sendResponse(response , HttpServletResponse.SC_OK , gson.toJson(exchangeRateDtoList));
                log.info("Successfully retrieved all exchangeRates in doGet");
            } else {
                String exchangeRateCode = path.substring(1);
                Optional<ExchangeRateDto> exchangeRateDto = exchangeRateService.getExchangeRateByCode(exchangeRateCode);
                //TODO код 400 нужно будет сделать в фильтре
                if(exchangeRateDto.isPresent()){
                    sendResponse(response , HttpServletResponse.SC_OK , gson.toJson(exchangeRateDto.get()));
                    log.info("Successfully retrieved exchange rate in doGet for exchangeRateCode: {}", exchangeRateCode);
                }else {
                    sendResponse(response , HttpServletResponse.SC_NOT_FOUND , gson.toJson(new ExchangeRateDto()));
                    log.warn("No exchange rate found for exchangeRateCode: {}", exchangeRateCode);
                }
            }
        }catch (IOException e){
            log.error(e.getMessage());
            sendResponse(response , HttpServletResponse.SC_INTERNAL_SERVER_ERROR , gson.toJson(e));
        }
    }


    private void sendResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.getWriter().write(message);
    }
}
