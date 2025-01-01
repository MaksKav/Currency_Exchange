package com.maxkavun.util;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponceUtil {
    private ResponceUtil() {
    }

    public static  void sendResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.getWriter().write(message);
    }
}
