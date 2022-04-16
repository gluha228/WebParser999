package com.db;

import com.parser.AllItemsParser;
import com.parser.SellingItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/*
если данным более 1 часа или их нет, то они запрашиваются с 999 заново
этот модуль встает между контроллером и парсером
 */
@Component
public class DBRequester {
    private final JdbcTemplate template;
    private final Map<String, Date> actuality = new HashMap<>();
    private final Map<String, String> validName = new HashMap<>();
    private final AllItemsParser parser;

    @Autowired
    public DBRequester(AllItemsParser parser) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:tcp://localhost/~/test");
        dataSource.setUsername("user");
        dataSource.setPassword("user");
        this.template = new JdbcTemplate(dataSource);
        this.parser = parser;
    }


    private String intoValidName(String category) {
        String name = "";
        for (int i = 0; i < category.length(); i++) {
            if (category.charAt(i) >= 'a' && category.charAt(i) <= 'z') name += category.charAt(i);
        }
        return name;
    }

    private void insertItem(SellingItem item, String category) {
        template.update("INSERT INTO " + validName.get(category) + " VALUES(?, ?, ?, ?, ?)",
                item.getPrice(), item.getTitle(), item.getPhoneNumber(),
                item.getDescription(), item.getSeller());
    }

    private void createTable(String category) throws IOException {
        validName.put(category, intoValidName(category));
        template.execute("DROP TABLE IF EXISTS "+ validName.get(category) +";");
        template.execute("CREATE TABLE " + validName.get(category) + "(" +
                "price varchar, title varchar, phoneNumber varchar," +
                " description varchar , seller varchar );");
        parser.fullParseItems(category).forEach( item -> insertItem(item, category));
    }

    private void updateTable(String category) throws IOException {
        template.execute("TRUNCATE TABLE " + validName.get(category));
        parser.fullParseItems(category).forEach(item -> insertItem(item, category));
    }


    private List<SellingItem> getItemsFromCategory(String category) throws IOException {
        Date now = new Date(System.currentTimeMillis());
        if (actuality.containsKey(category)) {
            int updateTime = 60; // в минутах, сделал отдельной переменной для ясности кода
            if (TimeUnit.MINUTES.convert(actuality.get(category).getTime() - now.getTime(), TimeUnit.MILLISECONDS) > updateTime) {
                updateTable(category);
                actuality.put(category, now);
            }
        } else {
            createTable(category);
            actuality.put(category, now);
        }
        return template.query("SELECT * FROM " + validName.get(category), new BeanPropertyRowMapper<>(SellingItem.class));
    }


    public List getItems(String category, String mode) throws IOException {
        if (Objects.equals(mode, "full")) return getItemsFromCategory(category);
        return parser.previewItems(category);
    }



}
