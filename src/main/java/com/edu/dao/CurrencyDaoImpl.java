package com.edu.dao;

import com.edu.config.ConnectionDao;
import com.edu.config.ConnectionDaoSqlLiteImpl;
import com.edu.model.Currency;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class CurrencyDaoImpl implements SpecificCurrencyDao<Currency> {
    private final ConnectionDao connectionDao;
    private static final String GET_ALL_CURRENCIES = "SELECT * FROM Currencies";
    private static final String GET_BY_CODE = "SELECT * FROM Currencies where code = ?";
    private static final String SAVE = "INSERT INTO Currencies (Code, FullName, Sign) VALUES(?, ?, ?)" +
            "RETURNING id";
    private final String GET_BY_ID = "SELECT * FROM Currencies where id = ?";

    public CurrencyDaoImpl() {
        this.connectionDao = new ConnectionDaoSqlLiteImpl();
    }

    @Override
    public Stream<Currency> getAll() throws SQLException {
        List<Currency> res = new ArrayList<>();
        Connection connection =  connectionDao.getConnectionDB();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(GET_ALL_CURRENCIES);

        while (resultSet.next()) {
            Long id  = resultSet.getLong("ID");
            String code = resultSet.getString("Code");
            String fullName = resultSet.getString("FullName");
            String sign = resultSet.getString("Sign");
            res.add(new Currency(id, code, fullName, sign));
        }

        statement.close();
        connection.close();

        return res.stream();
    }

    @Override
    public Optional<Currency> save(Currency currency) throws SQLException {
        Connection connection = connectionDao.getConnectionDB();
        PreparedStatement preparedStatement = connection.prepareStatement(SAVE );
        preparedStatement.setString(1, currency.getCode());
        preparedStatement.setString(2, currency.getFullName());
        preparedStatement.setString(3, currency.getSign());

        ResultSet res = preparedStatement.executeQuery();
        Long id = res.getLong("id");
        currency.setId(id);

        preparedStatement.close();
        connection.close();

        return Optional.of(currency);
    }

    @Override
    public Optional<Currency> update(Currency currency) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Optional<Currency> delete(Currency currency) throws SQLException {
        return Optional.empty();
    }

    @Override
    public Optional<Currency> getById(Long id) throws SQLException {
        Connection connection = connectionDao.getConnectionDB();
        PreparedStatement prepareStatement = connection.prepareStatement(GET_BY_ID);
        prepareStatement.setLong(1, id);
        ResultSet resultSet = prepareStatement.executeQuery();

        if (!resultSet.next()) {
            prepareStatement.close();
            connection.close();
            return Optional.empty();
        }

        String code = resultSet.getString("code");
        String fullName = resultSet.getString("FullName");
        String sign = resultSet.getString("Sign");

        prepareStatement.close();
        connection.close();

        return Optional.of(new Currency(id, code, fullName, sign));
    }

    @Override
    public Optional<Currency> getByCode(String code) throws SQLException {
        Connection connection = connectionDao.getConnectionDB();
        PreparedStatement prepareStatement = connection.prepareStatement(GET_BY_CODE);
        prepareStatement.setString(1, code);
        ResultSet resultSet = prepareStatement.executeQuery();

        if (!resultSet.next()) {
            prepareStatement.close();
            connection.close();
            return Optional.empty();
        }

        Long id = resultSet.getLong("id");
        String fullName = resultSet.getString("FullName");
        String sign = resultSet.getString("Sign");

        prepareStatement.close();
        connection.close();

       return Optional.of(new Currency(id, code, fullName, sign));
    }
}
