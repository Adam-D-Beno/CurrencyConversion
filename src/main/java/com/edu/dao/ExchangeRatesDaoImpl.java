package com.edu.dao;

import com.edu.config.ConnectionDao;
import com.edu.config.ConnectionDaoSqlLiteImpl;
import com.edu.model.ExchangeRates;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ExchangeRatesDaoImpl implements SpecificExchangeRatesDao<ExchangeRates> {
    private ConnectionDao connectionDao;
    String crossCurrency = "USD";
    private static final String GET_ALL_EXCHANGE_RATES = "SELECT * FROM ExchangeRates";
    private  static final String INNER_JOIN_EXCHANGE_RATES_WITH_CURRENCIES = "SELECT ex.ID AS ID, " +
            "ex.BaseCurrencyId AS Base_id, " +
            "bc.Code AS Base_code, ex.TargetCurrencyId AS Target_id, tc.Code AS Target_code, ex.Rate\n" +
            "from ExchangeRates ex\n" +
            "    JOIN Currencies bc ON ex.BaseCurrencyId = bc.ID\n" +
            "    JOIN Currencies tc ON ex.TargetCurrencyId = tc.ID\n" +
            "    where bc.Code = ? and tc.Code = ?;";
    private static final String SAVE = "INSERT INTO ExchangeRates (BaseCurrencyId, rate, TargetCurrencyId) VALUES (?,?,?) " +
            "RETURNING id";

    private static final String UPDATE = "UPDATE ExchangeRates\n" +
            "SET Rate = ?\n" +
            "WHERE BaseCurrencyId = ? and TargetCurrencyId = ?\n" +
            "RETURNING ID;";


    public ExchangeRatesDaoImpl() {
        this.connectionDao = new ConnectionDaoSqlLiteImpl();
    }

    @Override
    public Stream<ExchangeRates> getAll() throws SQLException {
        Connection connection = connectionDao.getConnectionDB();
        Statement statement = connection.createStatement();
        List<ExchangeRates> res = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery(GET_ALL_EXCHANGE_RATES);

        while (resultSet.next()) {
            Long id = resultSet.getLong("id");
            Long baseCurrencyId = resultSet.getLong("BaseCurrencyId");
            BigDecimal rate = resultSet.getBigDecimal("Rate");
            Long targetCurrencyId = resultSet.getLong("TargetCurrencyId");

            res.add(new ExchangeRates(id, baseCurrencyId, targetCurrencyId, rate));
        }
        statement.close();
        connection.close();

        return res.stream();
    }

    @Override
    public Optional<ExchangeRates> save(ExchangeRates exchangeRates) throws SQLException {
        Connection connection = connectionDao.getConnectionDB();

        PreparedStatement preparedStatement = connection.prepareStatement(SAVE);
        preparedStatement.setLong(1, exchangeRates.getBaseCurrencyId());
        preparedStatement.setBigDecimal(2, exchangeRates.getRate());
        preparedStatement.setLong(3, exchangeRates.getTargetCurrencyId());

        ResultSet resultSet = preparedStatement.executeQuery();
        Long id = resultSet.getLong("id");
        exchangeRates.setId(id);

        preparedStatement.close();
        connection.close();

        return Optional.of(exchangeRates);
    }

    @Override
    public Optional<ExchangeRates> update(ExchangeRates exchangeRates) throws SQLException {
        Connection connection = connectionDao.getConnectionDB();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE);
        preparedStatement.setBigDecimal(1, exchangeRates.getRate());
        preparedStatement.setLong(2, exchangeRates.getBaseCurrencyId());
        preparedStatement.setLong(3, exchangeRates.getTargetCurrencyId());
        ResultSet resultSet = preparedStatement.executeQuery();

        Long id = resultSet.getLong("id");
        exchangeRates.setId(id);

        preparedStatement.close();
        connection.close();

        return Optional.of(exchangeRates);
    }

    @Override
    public Optional<ExchangeRates> delete(ExchangeRates exchangeRates) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Optional<ExchangeRates> getBySpecificExchangeRate(String baseCurrency, String targetCurrency) throws SQLException {

        Connection connection = connectionDao.getConnectionDB();
        PreparedStatement preparedStatement = connection.prepareStatement(INNER_JOIN_EXCHANGE_RATES_WITH_CURRENCIES);

        preparedStatement.setString(1, baseCurrency);
        preparedStatement.setString(2, targetCurrency);
        ResultSet res = preparedStatement.executeQuery();

        if (!res.next()) {
            preparedStatement.close();
            connection.close();
            return Optional.empty();
        }

        Long id = res.getLong("id");
        Long baseCurrencyId = res.getLong("Base_id");
        Long targetCurrencyId = res.getLong("Target_id");
        BigDecimal rate = res.getBigDecimal("Rate");

        preparedStatement.close();
        connection.close();

        return Optional.of(new ExchangeRates(id, baseCurrencyId, targetCurrencyId, rate));
    }

    @Override
    public List<Optional<ExchangeRates>>getBySpecificExchangeRateForCrossUSD(String baseCurrency, String targetCurrency) throws SQLException {

        Optional<ExchangeRates> from = getBySpecificExchangeRate(crossCurrency, baseCurrency);
        Optional<ExchangeRates> to = getBySpecificExchangeRate(crossCurrency, targetCurrency);

        return List.of(from, to);
    }
}
