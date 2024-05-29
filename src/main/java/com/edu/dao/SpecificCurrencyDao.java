package com.edu.dao;

import java.sql.SQLException;
import java.util.Optional;

public interface SpecificCurrencyDao<T> extends CrudDao<T>{
    Optional<T> getByCode(String code) throws SQLException;
    Optional<T> getById(Long id) throws SQLException;


}
