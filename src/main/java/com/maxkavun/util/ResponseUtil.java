package com.maxkavun.util;

import com.google.gson.Gson;
import com.maxkavun.dto.ExchangeErrorResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtil {
    private static final Gson gson = new Gson();

    private ResponseUtil() {
    }

    public static  void sendResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.getWriter().write(message);
    }

    public static  void sendErrorResponse(HttpServletResponse response, int status, String errorMessage) throws IOException {
        String errorJson = gson.toJson(new ExchangeErrorResponse(errorMessage));
        response.setStatus(status);
        response.getWriter().write(errorJson);
    }
}
