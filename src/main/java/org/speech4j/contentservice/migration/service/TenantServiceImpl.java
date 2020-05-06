package org.speech4j.contentservice.migration.service;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

@Service
public class TenantServiceImpl implements TenantService{

    @Override
    public Set<String> getAllTenants(DataSource dataSource) throws SQLException {
        Set<String> tenants = new HashSet<>();

        try (final Connection connection = dataSource.getConnection()){
            connection.setSchema("metadata");
            try(Statement statement = connection.createStatement()) {
                try(ResultSet resultSet = statement.executeQuery("SELECT * FROM tenants")) {

                    while (resultSet.next()) {
                        tenants.add(resultSet.getString("id"));
                    }
                }
            }
        }

        return tenants;
    }
}
