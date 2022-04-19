package com.db.sql;

import com.parser.SellingItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SellingItemCRUD {
    private final JdbcTemplate template;

    @Autowired
    public SellingItemCRUD(MyDataSource dataSourse) {
        this.template = dataSourse.getTemplate();
    }

    private String intoValidName(String category) {
        return category.replaceAll("[^a-z]", "");
    }

    public void createTable(String category) {
        template.execute("CREATE TABLE IF NOT EXISTS " + intoValidName(category) + "(" +
                "price varchar, title varchar, phoneNumber varchar," +
                " description varchar , seller varchar );");
    }

    public void addItem(SellingItem item, String category) {
        template.update("INSERT INTO " + intoValidName(category) + " VALUES(?, ?, ?, ?, ?)",
                item.getPrice(), item.getTitle(), item.getPhoneNumber(),
                item.getDescription(), item.getSeller());
    }

    public List<SellingItem> readItems(String category) {
        return template.query("SELECT * FROM " + intoValidName(category),
                new BeanPropertyRowMapper<>(SellingItem.class));
    }

    public void deleteItem(SellingItem item, String category) {
        template.update("DELETE FROM " + intoValidName(category) + " WHERE (price = ? and title = ? and seller = ? " +
                        "and phoneNumber = ? and description = ?) ", item.getPrice(), item.getTitle(), item.getSeller(),
                item.getPhoneNumber(), item.getDescription());
    }

}
