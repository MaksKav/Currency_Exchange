package com.maxkavun.dao;

import com.maxkavun.model.Currency;
import com.maxkavun.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CurrencyDao implements Dao<Integer, Currency> {

    private static final CurrencyDao INSTANCE = new CurrencyDao();

    private CurrencyDao() {
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    private static final String FIND_ALL_SQL = """
            SELECT id, code, full_name ,sign
            FROM   Currencies ;
            """;

    private static final String FIND_BY_ID_SQL = """
                        SELECT id, code, full_name , sign
                        FROM Currencies 
                        WHERE id = ? ;
            """;

    private static final String SAVE_SQL = """
            INSERT INTO Currencies (code, full_name, sign)
            VALUES(? , ? , ?);
            """;

    private static final String DELETE_BY_ID_SQL = """
            DELETE
            FROM Currencies
            WHERE id = ?;
            """;

    private static final String UPDATE_BY_ID_SQL = """
            UPDATE Currencies
            SET code = ?, full_name = ?, sign = ?
            WHERE id = ?;
            """;


    @Override
    public List<Currency> findAll() {
        try (var connection = ConnectionManager.getConnection();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL);
             var resultSet = prepareStatement.executeQuery()) {

            List<Currency> currencies = new ArrayList<>();
            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }

            return currencies.isEmpty() ? Collections.emptyList() : currencies;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Currency buildCurrency(ResultSet resultSet) {
        try {
            return new Currency(
                    resultSet.getObject("id", Integer.class),
                    resultSet.getObject("code", String.class),
                    resultSet.getObject("full_name", String.class),
                    resultSet.getObject("sign", String.class));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Currency> findById(Integer id) {
        try (var connection = ConnectionManager.getConnection();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setInt(1, id);

            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(buildCurrency(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Currency save(Currency model) {
        try (var connection = ConnectionManager.getConnection();
             var prepareStatement = connection.prepareStatement(SAVE_SQL)) {

            prepareStatement.setString(1, model.getCode());
            prepareStatement.setString(2, model.getFullName());
            prepareStatement.setString(3, model.getSign());

            int rows = prepareStatement.executeUpdate();
            if (rows > 0) {
                try (var generatedKeys = prepareStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        model.setId(generatedKeys.getInt(1));
                    }
                }
            }
            return model;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        try (var connection = ConnectionManager.getConnection();
             var prepareStatement = connection.prepareStatement(DELETE_BY_ID_SQL)) {
            prepareStatement.setInt(1, id);

            int rows = prepareStatement.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Currency model) {
        try (var connection = ConnectionManager.getConnection();
             var prepareStatement = connection.prepareStatement(UPDATE_BY_ID_SQL)) {
            prepareStatement.setString(1, model.getCode());
            prepareStatement.setString(2, model.getFullName());
            prepareStatement.setString(3, model.getSign());
            prepareStatement.setInt(4, model.getId());

            int rows = prepareStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
