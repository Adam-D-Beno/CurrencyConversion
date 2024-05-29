package com.edu.servlet;

import com.edu.dto.ExchangeRatesDTO;
import com.edu.exception.CurrencyNotExistInDataBase;
import com.edu.exception.WrongCurrencyCode;
import com.edu.exception.WrongFormFields;
import com.edu.service.ServiceExchangeRates;
import com.edu.dto.ResponseErrorDto;
import com.edu.validation.ValidateExchangeRate;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ExchangeRateServlet", urlPatterns = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ServiceExchangeRates serviceExchangeRates;
    private final ObjectMapper objectMapper;
    private final ValidateExchangeRate validateExchangeRate;

    public ExchangeRateServlet() {
        serviceExchangeRates = new ServiceExchangeRates();
        objectMapper = new ObjectMapper();
        validateExchangeRate = new ValidateExchangeRate();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String method = req.getMethod();
        if(!method.equals("PATCH")) {
            super.service(req,resp);
            return;
        }
        this.doPatch(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {

            validateExchangeRate.validateExchangeRate(req);
            List<String> currencyCodes = getCurrencyCodes(req);

            ExchangeRatesDTO exchangeRatesDTO = serviceExchangeRates.getSpecificExchangeRate(currencyCodes);
            resp.setStatus(200);
            resp.getWriter().println("Успех - " + resp.getStatus());
            resp.getWriter().println(objectMapper.writeValueAsString(exchangeRatesDTO));

        } catch (WrongCurrencyCode e) {
            resp.setStatus(400);
            resp.getWriter().println(objectMapper.writeValueAsString(
                    new ResponseErrorDto(e.getMessage(), resp.getStatus())));

        } catch (CurrencyNotExistInDataBase e) {
            resp.setStatus(404);
            resp.getWriter().println(objectMapper.writeValueAsString(
                    new ResponseErrorDto(e.getMessage(), resp.getStatus())));

        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ResponseErrorDto("база данных недоступна - ", resp.getStatus())));
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException{

        try {
            validateExchangeRate.validateExchangeRate(req);
            List<String> currencyCodes = getCurrencyCodes(req);
            var requestParameter = (req.getReader().readLine());
            validateExchangeRate.validateRateFiled(requestParameter);

           BigDecimal rate = getExchangeRates(requestParameter);

           ExchangeRatesDTO exchangeRatesDTO = serviceExchangeRates.updateExchangeRate(currencyCodes, rate);

           resp.setStatus(200);
           resp.getWriter().println(objectMapper.writeValueAsString(exchangeRatesDTO));

        } catch (WrongCurrencyCode | WrongFormFields e) {
            resp.setStatus(400);
            resp.getWriter().println(objectMapper.writeValueAsString(
                    new ResponseErrorDto(e.getMessage(), resp.getStatus())));

        } catch (CurrencyNotExistInDataBase e) {
            resp.setStatus(404);
            resp.getWriter().println(objectMapper.writeValueAsString(
                    new ResponseErrorDto(e.getMessage(), resp.getStatus())));

        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ResponseErrorDto("база данных недоступна - ", resp.getStatus())));
        }
    }

    private List<String> getCurrencyCodes(HttpServletRequest req) {
        String baseCurrencyCode = req.getHttpServletMapping().getMatchValue().substring(0,3).toUpperCase().trim();
        String TargetCurrencyCode = req.getHttpServletMapping().getMatchValue().substring(3,6).toUpperCase().trim();

        return List.of(baseCurrencyCode, TargetCurrencyCode);
    }

    private BigDecimal getExchangeRates(String requestParameter) {
        var rate = requestParameter.split("=")[1].replaceAll("%20"," ");
        return BigDecimal.valueOf(Double.parseDouble(rate));
    }
}
