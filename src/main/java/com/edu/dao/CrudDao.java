package com.edu.dao;

import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Stream;

public interface CrudDao<T> {

    Stream<T> getAll() throws SQLException;

    Optional<T> save(T t) throws SQLException;

    Optional<T> update(T t) throws SQLException;

    Optional<T> delete(T t) throws SQLException;




}
