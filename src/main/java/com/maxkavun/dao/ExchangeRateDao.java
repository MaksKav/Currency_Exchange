package com.maxkavun.dao;

import com.maxkavun.exception.ApplicationException;
import com.maxkavun.exception.DataAccessException;
import com.maxkavun.model.ExchangeRate;
import com.maxkavun.util.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao implements Dao<Integer, ExchangeRate> {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRateDao.class);

    public ExchangeRateDao() {
    }

    private static final String FIND_ALL_SQL = """
            SELECT id, base_currency_id, target_currency_id, rate
            FROM ExchangeRates;
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT id, base_currency_id, target_currency_id, rate
            FROM ExchangeRates
            WHERE id = ?;
            """;

    private static final String SAVE_SQL = """
            INSERT INTO ExchangeRates ( base_currency_id, target_currency_id, rate)
            VALUES (?, ?, ?);
            """;

    private static final String UPDATE_BY_ID_SQL = """
            UPDATE ExchangeRates
            SET base_currency_id = ?, target_currency_id = ?, rate = ?
            WHERE id = ?;
            """;

    private static final String FIND_BY_CODE_SQL = """
            SELECT er.id, er.base_currency_id, er.target_currency_id, er.rate 
            FROM ExchangeRates er
            JOIN Currencies bc ON er.base_currency_id = bc.id
            JOIN Currencies tc ON er.target_currency_id = tc.id
            WHERE bc.code = ? AND tc.code = ?
            """;

    private static final String DELETE_BY_ID_SQL = """
            DELETE 
            FROM ExchangeRates
            WHERE id = ?;
            """;


    @Override
    public Optional<ExchangeRate> findByCode(String code) {
        try (var connection = ConnectionManager.getConnection(); var prepareStatement = connection.prepareStatement(FIND_BY_CODE_SQL)) {
            String baseCurrencyCode = code.substring(0, 3);
            String targetCurrencyCode = code.substring(3);
            prepareStatement.setString(1, baseCurrencyCode);
            prepareStatement.setString(2, targetCurrencyCode);
            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new ExchangeRate(resultSet.getObject("id", Integer.class), resultSet.getObject("base_currency_id", Integer.class), resultSet.getObject("target_currency_id", Integer.class), resultSet.getObject("rate", BigDecimal.class)));
                }else {
                    return Optional.empty();
                }            }
        } catch (SQLException e) {
            throw new DataAccessException("Exception while fetching exchange rate from database", e);
        }
    }

    @Override
    public List<ExchangeRate> findAll() {
        try (var connection = ConnectionManager.getConnection(); var prepareStatement = connection.prepareStatement(FIND_ALL_SQL); var resultSet = prepareStatement.executeQuery()) {

            List<ExchangeRate> exchangeRates = new ArrayList<>();
            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRate(resultSet));
            }
            log.info("Fetched exchangeRates: {}", exchangeRates);
            return exchangeRates.isEmpty() ? Collections.emptyList() : exchangeRates;
        } catch (SQLException e) {
            log.error("Failed to find all exchangeRates: ", e);
            throw new RuntimeException(e);
        }
    }

    private ExchangeRate buildExchangeRate(ResultSet resultSet) {
        try {
            return new ExchangeRate(resultSet.getObject("id", Integer.class), resultSet.getObject("base_currency_id", Integer.class), resultSet.getObject("target_currency_id", Integer.class), resultSet.getObject("rate", BigDecimal.class));
        } catch (SQLException e) {
            log.error("Failed to build exchangeRate: ", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<ExchangeRate> findById(Integer id) {
        try (var connection = ConnectionManager.getConnection(); var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setInt(1, id);

            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    log.info("Found exchange rate with id: {}", id);
                    return Optional.of(buildExchangeRate(resultSet));
                }
            }
            log.warn("Failed to find exchange rate with id: {}", id);
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Failed to find exchange rate: ", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public ExchangeRate save(ExchangeRate model) {
        try (var connection = ConnectionManager.getConnection(); var prepareStatement = connection.prepareStatement(SAVE_SQL)) {
            prepareStatement.setInt(1, model.getBaseCurrencyId());
            prepareStatement.setInt(2, model.getTargetCurrencyId());
            prepareStatement.setBigDecimal(3, model.getRate());

            int rows = prepareStatement.executeUpdate();
            if (rows > 0) {
                try (var generatedKeys = prepareStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        log.info("Saved exchange rate with id: {}", model.getBaseCurrencyId());
                        model.setId(generatedKeys.getInt(1));
                    } else {
                        log.warn("Failed to save exchange rate with id: {}", model.getBaseCurrencyId());
                    }
                }
            }
            return model;
        } catch (SQLException e) {
            log.error("Failed to save exchangeRate: ", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public void update(ExchangeRate model) {
        try (var connection = ConnectionManager.getConnection(); var prepareStatement = connection.prepareStatement(UPDATE_BY_ID_SQL)) {
            prepareStatement.setInt(1, model.getBaseCurrencyId());
            prepareStatement.setInt(2, model.getTargetCurrencyId());
            prepareStatement.setBigDecimal(3, model.getRate());
            prepareStatement.setInt(4, model.getId());

            int rows = prepareStatement.executeUpdate();
            if (rows > 0) {
                log.info("Updated exchange rate with id: {}", model.getId());
            } else {
                log.warn("Failed to update exchange rate with id: {}", model.getId());
            }

        } catch (SQLException e) {
            log.error("Failed to update exchangeRate: ", e);
            throw new ApplicationException(e.getMessage()) {
            };
        }
    }


    @Override
    public boolean deleteById(Integer id) {
        try (var connection = ConnectionManager.getConnection(); var prepareStatement = connection.prepareStatement(DELETE_BY_ID_SQL)) {
            prepareStatement.setInt(1, id);

            int rows = prepareStatement.executeUpdate();
            if (rows > 0) {
                log.info("Deleted exchange rate with id: {}", id);
                return true;
            } else {
                log.warn("Failed to delete exchange rate with id: {}", id);
                return false;
            }
        } catch (SQLException e) {
            log.error("Failed to delete exchangeRate: ", e);
            throw new ApplicationException(e.getMessage()) {
            };
        }
    }
}
