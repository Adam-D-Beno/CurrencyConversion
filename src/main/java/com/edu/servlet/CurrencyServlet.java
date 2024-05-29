package com.edu.servlet;

import com.edu.dto.CurrencyDTO;
import com.edu.exception.CurrencyNotExistInDataBase;
import com.edu.exception.WrongCurrencyCode;
import com.edu.service.ServiceCurrencies;
import com.edu.dto.ResponseErrorDto;
import com.edu.validation.ValidationCurrencies;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;


@WebServlet(name = "CurrencyServlet", urlPatterns = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final ServiceCurrencies serviceCurrencies;
    private final ObjectMapper objectMapper;
    private final ValidationCurrencies validationCurrencies;

    public CurrencyServlet() {
        serviceCurrencies = new ServiceCurrencies();
        objectMapper = new ObjectMapper();
        validationCurrencies = new ValidationCurrencies();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {

            validationCurrencies.validationCurrency(req);
            String currency = getCurrencyCode(req).trim().toUpperCase();
            CurrencyDTO currencyDTO = serviceCurrencies.getSpecificCurrency(currency);
            resp.setStatus(200);
            resp.getWriter().println("Успех -" + resp.getStatus());
            resp.getWriter().print(objectMapper.writeValueAsString(currencyDTO));

        } catch (WrongCurrencyCode e) {
            resp.setStatus(400);
            resp.getWriter().println(objectMapper.writeValueAsString(
                    new ResponseErrorDto(e.getMessage(), resp.getStatus())));

        } catch (CurrencyNotExistInDataBase e) {
           resp.setStatus(404);
           resp.getWriter().print(objectMapper.writeValueAsString(
                   new ResponseErrorDto(e.getMessage(), resp.getStatus())));

        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ResponseErrorDto("база данных недоступна - ", resp.getStatus())));
        }
    }

    private String getCurrencyCode(HttpServletRequest req) {
         return req.getHttpServletMapping().getMatchValue();
    }
}
