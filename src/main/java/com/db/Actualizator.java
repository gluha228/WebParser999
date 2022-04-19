package com.db;

import com.db.sql.ActualizatorCRUD;
import com.db.sql.SellingItemCRUD;
import com.parser.AllItemsParser;
import com.parser.EasySellingItem;
import com.parser.SellingItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class Actualizator {
    private final SellingItemCRUD crud;
    private final ActualizatorCRUD aCrud;
    private final List<String> parseProcesses = new ArrayList<>();
    private final Map<String, Date> tableInfo = new HashMap<>();
    private final AllItemsParser parser;

    @Autowired
    public Actualizator(AllItemsParser parser, SellingItemCRUD crud, ActualizatorCRUD aCrud) {
        this.parser = parser;
        this.crud = crud;
        this.aCrud = aCrud;
        aCrud.createTable();
        aCrud.readActualityInfo().forEach(info -> tableInfo.put(info.getUrl(), info.getLastUpdate()));
        sideUpdate();
    }

    //довольно долго, но минимизированы перезаписи
    //используется частичный парсинг(только страницы со всеми объявлениями), он дает только цену и название, потому есть небольшой риск
    private void updateTableActuality(String category) throws IOException {
        List<EasySellingItem> parseData = parser.previewItems(category);
        List<SellingItem> tableData = crud.readItems(category);
                //вычитание пересечений множеств
        parseData.forEach(parseItem -> tableData.forEach(tableItem -> {
            if (Objects.equals(tableItem.getPrice(), parseItem.getPrice()) &&
                    Objects.equals(tableItem.getTitle(), parseItem.getTitle())) {
                parseData.remove(parseItem);
                tableData.remove(tableItem);
            }}));

        tableData.forEach(item -> crud.deleteItem(item, category));
        parseData.forEach(item -> { try {
                crud.addItem(parser.getItem(item.getRef()), category);
            } catch (IOException e) { e.printStackTrace(); }});
    }

    public void checkTableActuality(String category) throws IOException {
        if (parseProcesses.contains(category)) return;
        createTableIfNotExist(category);
        Date now = new Date(System.currentTimeMillis());
        int updateTime = 60;
        if (TimeUnit.MINUTES.convert(now.getTime() - tableInfo.get(category).getTime(), TimeUnit.MILLISECONDS) > updateTime) {
            new Thread(() -> { try {
                parseProcesses.add(category);
                updateTableActuality(category);
                aCrud.updateActualityInfo(category, now);
                parseProcesses.remove(category);
            } catch (IOException e) { e.printStackTrace(); }}).start();
        }
    }

    private void createTableIfNotExist(String category) {
        if (!tableInfo.containsKey(category)) {
            crud.createTable(category);
            crud.addItem(new SellingItem("Перезагрузите страницу секунд через 30", "",
                    "", "", ""), category);
            tableInfo.put(category, new Date(0));
        }
    }

    //раз в полчаса проверяет актуальность данных в бд
    public void sideUpdate() {
        new Thread(() -> {
            while (true) {
                for (Map.Entry<String, Date> entry : tableInfo.entrySet()) try {
                    checkTableActuality(entry.getKey());
                    } catch (IOException e) { e.printStackTrace(); }
                try {
                    Thread.sleep(1000*60*30);}
                catch (InterruptedException e) {e.printStackTrace();}
            }
        }).start();
    }
}
