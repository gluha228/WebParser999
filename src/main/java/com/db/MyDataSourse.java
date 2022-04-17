package com.db;

import lombok.NoArgsConstructor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
@Component
@NoArgsConstructor
public class MyDataSourse {
    public DataSource getDataSourse() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:tcp://localhost/~/test");
        dataSource.setUsername("user");
        dataSource.setPassword("user");
        return dataSource;
    }
}
