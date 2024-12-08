package com.maxkavun.dao;

import com.maxkavun.model.ExchangeRate;
import com.maxkavun.util.ConnectionManager;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao implements Dao<Integer, ExchangeRate> {

    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();

    private ExchangeRateDao() {
    }

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
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

    private static final String DELETE_BY_ID = """
            DELETE 
            FROM ExchangeRates
            WHERE id = ?;
            """;


    @Override
    public List<ExchangeRate> findAll() {
        try (var connection = ConnectionManager.getConnection();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = prepareStatement.executeQuery();

            List<ExchangeRate> exchangeRates = new ArrayList<>();
            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRate(resultSet));
            }
            return exchangeRates.isEmpty() ? Collections.emptyList() : exchangeRates;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ExchangeRate buildExchangeRate(ResultSet resultSet) {
        try {
            return new ExchangeRate(
                    resultSet.getObject("id", Integer.class),
                    resultSet.getObject("base_currency_id", Integer.class),
                    resultSet.getObject("target_currency_id", Integer.class),
                    resultSet.getObject("rate", BigDecimal.class));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExchangeRate> findById(Integer id) {
        try(var connection = ConnectionManager.getConnection();
        var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)){
            prepareStatement.setInt(1, id);
            var resultSet = prepareStatement.executeQuery();
            if(resultSet.next()) {
                return Optional.of(buildExchangeRate(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExchangeRate save(ExchangeRate model) {
        try(var connection = ConnectionManager.getConnection();
            var prepareStatement = connection.prepareStatement(SAVE_SQL)){
            prepareStatement.setInt(1 , model.getBaseCurrencyId());
            prepareStatement.setInt(2 , model.getTargetCurrencyId());
            prepareStatement.setBigDecimal(3 , model.getRate());

          int rows = prepareStatement.executeUpdate();
          if(rows > 0 ) {
              try(var generatedKeys = prepareStatement.getGeneratedKeys()){
                  if(generatedKeys.next()) {
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
    public void update(ExchangeRate model) {
        try (var connection = ConnectionManager.getConnection();
             var prepareStatement = connection.prepareStatement(UPDATE_BY_ID_SQL)){
            prepareStatement.setInt(1 , model.getBaseCurrencyId());
            prepareStatement.setInt(2 , model.getTargetCurrencyId());
            prepareStatement.setBigDecimal(3 , model.getRate());
            prepareStatement.setInt(4 , model.getId());

            int rows = prepareStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        try(var connection = ConnectionManager.getConnection();
            var prepareStatement = connection.prepareStatement(DELETE_BY_ID)){
            prepareStatement.setInt(1 , id);

            int rows = prepareStatement.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
