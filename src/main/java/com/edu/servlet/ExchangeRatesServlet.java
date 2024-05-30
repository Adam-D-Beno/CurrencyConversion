package com.edu.servlet;

import com.edu.dto.ExchangeRatesDTO;
import com.edu.exception.CurrencyNotExistInDataBase;
import com.edu.exception.WrongFormFields;
import com.edu.service.ServiceExchangeRatesImpl;
import com.edu.dto.ResponseErrorDto;
import com.edu.validation.ValidateExchangeRate;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ExchangeRatesServlet", urlPatterns = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ServiceExchangeRatesImpl serviceExchangeRatesImpl;
    private final ObjectMapper objectMapper;
    private final ValidateExchangeRate validateExchangeRate;

    public ExchangeRatesServlet() {
        serviceExchangeRatesImpl = new ServiceExchangeRatesImpl();
        objectMapper = new ObjectMapper();
        validateExchangeRate = new ValidateExchangeRate();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            List<ExchangeRatesDTO> exchangeRatesDto = serviceExchangeRatesImpl.getListOfExchangeRates();
            resp.setStatus(200);
            resp.getWriter().println(objectMapper.writeValueAsString(exchangeRatesDto));

        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().println(objectMapper.writeValueAsString("база данных недоступна - " + resp.getStatus()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {

        try {
            validateExchangeRate.validateExchangeRate(req);

            List<String> currencyCodes = getCurrencyCodes(req);
            BigDecimal rate = getExchangeRates(req);

            ExchangeRatesDTO exchangeRatesDTO = serviceExchangeRatesImpl.
                    addNewExchangeRates(currencyCodes, rate);
            resp.setStatus(201);
            resp.getWriter().println("Успех -" + resp.getStatus());
            resp.getWriter().println(objectMapper.writeValueAsString(exchangeRatesDTO));

        } catch (WrongFormFields e) {
            resp.setStatus(400);
            resp.getWriter().println(objectMapper.writeValueAsString(
                    new ResponseErrorDto(e.getMessage(), resp.getStatus())));

        } catch (CurrencyNotExistInDataBase e) {
            resp.setStatus(404);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ResponseErrorDto(e.getMessage(), resp.getStatus())));

        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                resp.setStatus(409);
                resp.getWriter().println(objectMapper.
                     writeValueAsString(
                          new ResponseErrorDto("Валютная пара с таким кодом уже существует", resp.getStatus())));
                return;
            }
            resp.setStatus(500);
            resp.getWriter().println(objectMapper.writeValueAsString(
                    new ResponseErrorDto("база данных недоступна - ", resp.getStatus())));
        }
    }


    private List<String> getCurrencyCodes(HttpServletRequest req) {
        var baseCurrencyCode =req.getParameter("baseCurrencyCode").trim().toUpperCase();
        var targetCurrencyCode = req.getParameter("targetCurrencyCode").trim().toUpperCase();

        return List.of(baseCurrencyCode, targetCurrencyCode);
    }

    private static BigDecimal getExchangeRates(HttpServletRequest req) {
        return BigDecimal.valueOf(Double.parseDouble(req.getParameter("rate").trim()));
    }
}
