package com.edu.servlet;

import com.edu.dto.CurrencyExchangeDTO;
import com.edu.exception.CurrencyNotExistInDataBase;
import com.edu.exception.WrongFormFields;
import com.edu.exception.WrongRateInExchangeRate;
import com.edu.service.ServiceConvertCurrenciesImpl;
import com.edu.dto.ResponseErrorDto;
import com.edu.validation.ValidateExchange;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ExchangeServlet", urlPatterns = "/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ServiceConvertCurrenciesImpl serviceConvertCurrenciesImpl;
    private final ObjectMapper objectMapper;
    private final ValidateExchange validateExchange;

    public ExchangeServlet() {
        serviceConvertCurrenciesImpl = new ServiceConvertCurrenciesImpl();
        objectMapper = new ObjectMapper();
        validateExchange = new ValidateExchange();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
           validateExchange.validationExchange(req);

           var currencyCodes = getCurrencyCodes(req);
           var amount= getAmount(req);

           CurrencyExchangeDTO currencyExchangeDTO = serviceConvertCurrenciesImpl.convert(currencyCodes, amount);
           resp.setStatus(200);
           resp.getWriter().print(objectMapper.writeValueAsString("Успех - ") + resp.getStatus() + "\n");
           resp.getWriter().print(objectMapper.writeValueAsString(currencyExchangeDTO));

        } catch (CurrencyNotExistInDataBase e) {
            resp.setStatus(404);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ResponseErrorDto(e.getMessage(), resp.getStatus())));

        } catch (WrongFormFields e) {
            resp.setStatus(400);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ResponseErrorDto(e.getMessage(), resp.getStatus())));

        } catch (WrongRateInExchangeRate e) {
            resp.setStatus(502);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ResponseErrorDto(e.getMessage(), resp.getStatus())));

        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ResponseErrorDto("база данных недоступна - ", resp.getStatus())));
        }
    }

    private List<String> getCurrencyCodes(HttpServletRequest req) {
        String from = req.getParameter("from").trim().toUpperCase();
        String to = req.getParameter("to").trim().toUpperCase();
        return List.of(from, to);
    }

    private BigDecimal getAmount(HttpServletRequest req) {
        return BigDecimal.valueOf(Double.parseDouble(req.getParameter("amount").trim()));
    }
}
