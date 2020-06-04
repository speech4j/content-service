package org.speech4j.contentservice.migration.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.speech4j.contentservice.migration.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TenantServiceImpl implements TenantService {
    private DataSource dataSource;

    @Autowired
    public TenantServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<String> getAllTenants() throws SQLException {
        List<String> tenants = new ArrayList<>();

        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM metadata.tenants")
        ){
            while (resultSet.next()) {
                tenants.add(resultSet.getString("id"));
            }
        }

        log.debug("TENANT-SERVICE: All the tenants were retrieved successfully!");
        return tenants;
    }
}
