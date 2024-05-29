package com.edu.servlet;

import com.edu.dto.CurrencyDTO;
import com.edu.exception.WrongFormFields;
import com.edu.model.Currency;
import com.edu.service.ServiceCurrenciesImpl;
import com.edu.dto.ResponseErrorDto;
import com.edu.validation.ValidationCurrencies;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "CurrenciesServlet", urlPatterns = "/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final ServiceCurrenciesImpl serviceCurrenciesImpl;
    private final ObjectMapper objectMapper;
    private final ValidationCurrencies validationCurrencies;

    public CurrenciesServlet() {
       serviceCurrenciesImpl = new ServiceCurrenciesImpl();
       objectMapper = new ObjectMapper();
       validationCurrencies = new ValidationCurrencies();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<CurrencyDTO> currenciesDTO = serviceCurrenciesImpl.getListOfCurrencies();
            resp.setStatus(200);
            resp.getWriter().println("Успех " + resp.getStatus());
            resp.getWriter().println(objectMapper.writeValueAsString(currenciesDTO));

        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ResponseErrorDto("база данных недоступна - ", resp.getStatus())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
           validationCurrencies.validationCurrency(req);

           Currency currency = getCurrency(req);

           CurrencyDTO currencyDTO =  serviceCurrenciesImpl.addNewCurrencies(currency);
           resp.setStatus(201);
           resp.getWriter().println("Успех " + resp.getStatus());
           resp.getWriter().println(objectMapper.writeValueAsString(currencyDTO));

        } catch (WrongFormFields e) {
            resp.setStatus(400);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ResponseErrorDto(e.getMessage(), resp.getStatus())));

        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                resp.setStatus(409);
                resp.getWriter().print(objectMapper.writeValueAsString(
                        new ResponseErrorDto("Валюта с таким кодом уже существует - ", resp.getStatus())));
                return;
            }
                resp.setStatus(500);
                resp.getWriter().print(objectMapper.writeValueAsString(
                        new ResponseErrorDto("база данных недоступна - ", resp.getStatus())));
        }
    }

    private Currency getCurrency(HttpServletRequest req) {
        String name  = req.getParameter("name").trim();
        String code  = req.getParameter("code").trim();
        String sign  = req.getParameter("sign").trim();

        return new Currency(code, name,sign);
    }
}
