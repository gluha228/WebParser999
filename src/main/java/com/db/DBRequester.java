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

@Component
public class DBRequester {
    private final JdbcTemplate template;
    private final List<String> parseProcesses = new ArrayList<>();
    private Map<String, TableInfo> tableInfo = new HashMap<>();
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
        //таблица с инфой про актуальность остальных таблиц с данными, нужна чтобы при перезапуске сервера инфа об актуальности не пропадала, словно перезапуска и не было
        template.execute("CREATE TABLE IF NOT EXISTS ActualityInfo(lastUpdate varchar, originalUrl varchar)");
        template.query("SELECT * FROM ActualityInfo", new BeanPropertyRowMapper<>(TableInfo.class)).forEach(info ->
                tableInfo.put(intoValidName(info.getOriginalUrl()), info));
        sideUpdate();
    }

    private void addActualityInfo(String category){
        tableInfo.put(intoValidName(category), new TableInfo(new Date(0), category));
        template.update("INSERT INTO ActualityInfo VALUES(?, ?)", new Date(0), category);
        createTable(intoValidName(category));
    }

    private void updateActualityInfo(String category) {
        tableInfo.get(category).setLastUpdate(new Date(System.currentTimeMillis()));
        template.update("UPDATE ActualityInfo SET lastUpdate = ? WHERE originalUrl = ?",
                tableInfo.get(category).getLastUpdate(), tableInfo.get(category).getOriginalUrl());
    }

    //ссылку нельзя вставить как имя таблицы, так что удаляю все лишние символы. плюс так исключается возможность sql инекции
    private String intoValidName(String category) {
        String name = "";
        for (int i = 0; i < category.length(); i++) {
            if (category.charAt(i) >= 'a' && category.charAt(i) <= 'z') name += category.charAt(i);
        }
        return name;
    }

    private void insertItem(SellingItem item, String category) {
        template.update("INSERT INTO " + category + " VALUES(?, ?, ?, ?, ?)",
                item.getPrice(), item.getTitle(), item.getPhoneNumber(),
                item.getDescription(), item.getSeller());
    }

    private void createTable(String category) {
        template.execute("DROP TABLE IF EXISTS "+ category +";");
        template.execute("CREATE TABLE " + category + "(" +
                "price varchar, title varchar, phoneNumber varchar," +
                " description varchar , seller varchar );");
    }

    //при обновлении можно и сравнить со списком от парсера, добавив то, что есть только в списке и удалить то,
    // что есть только в бд, так конечно будет меньше перезаписей, однако будет намного дольше выполняться
    private void updateTable(String category) throws IOException {
        template.execute("TRUNCATE TABLE " + category);
        parser.fullParseItems(tableInfo.get(category).getOriginalUrl()).forEach(item -> insertItem(item, category));
    }

    private List<SellingItem> getItemsFromCategory(String category) throws IOException {
        Date now = new Date(System.currentTimeMillis());
        int updateTime = 60; // в минутах, сделал отдельной переменной для читаемости кода
        if (TimeUnit.MINUTES.convert(now.getTime() - tableInfo.get(category).getLastUpdate().getTime(), TimeUnit.MILLISECONDS) > updateTime) {
            if (!parseProcesses.contains(category))
                new Thread(() -> {
                    try {
                        parseProcesses.add(category);
                        updateTable(category);
                        updateActualityInfo(category);
                        parseProcesses.remove(category);
                    } catch (IOException e) { e.printStackTrace(); }
                }).start();
            if (tableInfo.get(category).getLastUpdate().getTime() == 0)
                return Arrays.asList(new SellingItem("Перезагрузите страницу секунд через 30", "", "", "", ""));
        }
        return template.query("SELECT * FROM " + category, new BeanPropertyRowMapper<>(SellingItem.class));
    }

    //раз в полчаса проверяет актуальность данных в бд
    public void sideUpdate() {
        new Thread(() -> {
            while (true) {
                for (Map.Entry<String, TableInfo> entry : tableInfo.entrySet()) try {
                    getItemsFromCategory(entry.getKey());
                    } catch (IOException e) { e.printStackTrace(); }
                try {
                    Thread.sleep(1000*60*30);}
                catch (InterruptedException e) {e.printStackTrace();}
            }
        }).start();
    }

    public List getItems(String category, String mode) throws IOException {
        if (Objects.equals(mode, "full")) {
            if (!tableInfo.containsKey(intoValidName(category))) addActualityInfo(category);
            return getItemsFromCategory(intoValidName(category));
        }
        return parser.previewItems(category);
    }
}
