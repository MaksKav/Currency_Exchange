package com.maxkavun.dao;

import com.maxkavun.exception.DataAccessException;
import com.maxkavun.model.Currency;
import com.maxkavun.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrencyDao implements Dao<Integer, Currency> {

    private static final Logger log = LoggerFactory.getLogger(CurrencyDao.class);

    public  CurrencyDao() {
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

    private static final String FIND_BY_CODE_SQL = """
            SELECT id, code, full_name , sign
            FROM Currencies
            WHERE code = ? ;
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

            log.info("Fetched all currencies: {}", currencies);
            return currencies.isEmpty() ? Collections.emptyList() : currencies;

        } catch (SQLException e) {
            log.error("Error fetching all currencies", e);
            throw new DataAccessException("Failed to fetch all currencies from the database in FindAll method " + e.getMessage());

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
            log.error("Error building currency from result set", e);
            throw new DataAccessException("Failed to build currency from result set in buildCurrency method " + e.getMessage());
        }
    }


    @Override
    public Optional<Currency> findById(Integer id) {
        try (var connection = ConnectionManager.getConnection();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setInt(1, id);

            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    log.info("Currency found with id {}: {}", id, buildCurrency(resultSet));
                    return Optional.of(buildCurrency(resultSet));
                }
                log.info("No currency found with id {}", id);
                return Optional.empty();
            }
        } catch (SQLException e) {
            log.error("Error fetching currency with id {}", id, e);
            throw new DataAccessException("Failed to fetch currency with id:  " + id + " " + e.getMessage());
        }
    }


    public Optional<Currency> findByCode(String code) {
        try (var connection = ConnectionManager.getConnection();
             var prepareStatement = connection.prepareStatement(FIND_BY_CODE_SQL)) {
            prepareStatement.setString(1, code);

            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    log.info("Currency found with code {}: {}", code, buildCurrency(resultSet));
                    return Optional.of(buildCurrency(resultSet));
                }
                log.info("No currency found with code {}", code);
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch currency with code: " + code + " " + e.getMessage());
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
                log.info("Currency saved successfully: {}", model);
            }
            return model;
        } catch (SQLException e) {
            log.error("Error saving currency: {}", model, e);
            throw new DataAccessException("Failed to save currency: " + model + " " + e.getMessage());
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        try (var connection = ConnectionManager.getConnection();
             var prepareStatement = connection.prepareStatement(DELETE_BY_ID_SQL)) {
            prepareStatement.setInt(1, id);

            int rows = prepareStatement.executeUpdate();
            if (rows > 0) {
                log.info("Currency with id {} deleted successfully", id);
                return true;
            } else {
                log.warn("Currency with id {} not found", id);
                return false;
            }
        } catch (SQLException e) {
            log.error("Error deleting currency with id {}", id, e);
            throw new DataAccessException("Failed to delete currency with id: " + id + " " + e.getMessage());
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
            if (rows > 0) {
                log.info("Currency with id {} updated successfully: {}", model.getId(), model);
            } else {
                log.warn("No currency found with id {} to update", model.getId());
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update currency with id: " + model.getId() + " " + e.getMessage());
        }
    }
}
