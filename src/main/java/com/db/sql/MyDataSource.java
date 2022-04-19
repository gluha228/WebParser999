package com.db.sql;

import lombok.Getter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

@Component
@Getter
public class MyDataSource {
    private JdbcTemplate template;
    public MyDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:tcp://localhost/~/test");
        dataSource.setUsername("user");
        dataSource.setPassword("user");
        this.template = new JdbcTemplate(dataSource);
    }
}
