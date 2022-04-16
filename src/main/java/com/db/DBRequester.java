package com.db;

import com.parser.AllItemsParser;
import com.parser.SellingItem;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class DBRequester {
    private final JdbcTemplate template;
    private final Map<String, Date> actuality = new HashMap<>();
    private final Map<String, String> validName = new HashMap<>();
    private final List<String> parseProcesses = new ArrayList<>();
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

    private void createTable(String category) {
        validName.put(category, intoValidName(category));
        template.execute("DROP TABLE IF EXISTS "+ validName.get(category) +";");
        template.execute("CREATE TABLE " + validName.get(category) + "(" +
                "price varchar, title varchar, phoneNumber varchar," +
                " description varchar , seller varchar );");
    }

    private void updateTable(String category) throws IOException {
        template.execute("TRUNCATE TABLE " + validName.get(category));
        parser.fullParseItems(category).forEach(item -> insertItem(item, category));
    }

    private List<SellingItem> getItemsFromCategory(String category) throws IOException {
        Date now = new Date(System.currentTimeMillis());
        if (!actuality.containsKey(category)) {
            createTable(category);
            actuality.put(category, new Date(0));
        }
        int updateTime = 60; // в минутах, сделал отдельной переменной для ясности кода
        if (TimeUnit.MINUTES.convert(now.getTime() - actuality.get(category).getTime(), TimeUnit.MILLISECONDS) > updateTime) {
            if (!parseProcesses.contains(category))
                new Thread(() -> {
                    try {
                        parseProcesses.add(category);
                        updateTable(category);
                        actuality.put(category, now);
                        parseProcesses.remove(category);
                    } catch (IOException e) { e.printStackTrace(); }
                }).start();
            if (actuality.get(category).getTime() == 0)
                return Arrays.asList(new SellingItem("Перезагрузите страницу секунд через 30", "", "", "", ""));
        }

        return template.query("SELECT * FROM " + validName.get(category), new BeanPropertyRowMapper<>(SellingItem.class));
    }

    public void sideUpdate() {
        new Thread(() -> {
            while (true) {
                for (Map.Entry<String, Date> entry : actuality.entrySet()) try {
                    getItemsFromCategory(entry.getKey());
                    } catch (IOException e) { e.printStackTrace(); }
                try {
                    Thread.sleep(1000*60*30);}   //раз в полчаса проверяет актуальность url-ов, которые уже запрашивались до этого
                catch (InterruptedException e) {e.printStackTrace();}
            }
        }).start();
    }

    public List getItems(String category, String mode) throws IOException {
        if (Objects.equals(mode, "full")) return getItemsFromCategory(category);
        return parser.previewItems(category);
    }



}
