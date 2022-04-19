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
public class Actualizer {
    private final SellingItemCRUD itemDB;
    private final ActualizatorCRUD actualityDB;
    private final List<String> parseProcesses = new ArrayList<>();
    private final Map<String, Date> tableInfo = new HashMap<>();
    private final AllItemsParser parser;

    @Autowired
    public Actualizer(AllItemsParser parser, SellingItemCRUD crud, ActualizatorCRUD aCrud) {
        this.parser = parser;
        this.itemDB = crud;
        this.actualityDB = aCrud;
        actualityDB.createTable();
        actualityDB.readActualityInfo().forEach(info -> tableInfo.put(info.getUrl(), info.getLastUpdate()));
        sideUpdate();
    }

    //довольно долго, но минимизированы перезаписи в дб
    //используется частичный парсинг(только общей страницы), он дает только цену и название
    private void updateTableActuality(String category) throws IOException {
        List<EasySellingItem> parseData = parser.previewItems(category);
        List<SellingItem> tableData = itemDB.readItems(category);
                //вычитание пересечений множеств
        parseData.forEach(parseItem -> tableData.forEach(tableItem -> {
            if (Objects.equals(tableItem.getPrice(), parseItem.getPrice()) &&
                    Objects.equals(tableItem.getTitle(), parseItem.getTitle())) {
                parseData.remove(parseItem);
                tableData.remove(tableItem);
            }}));

        List<SellingItem> itemsToAdd = new ArrayList<>();
        parseData.forEach(item -> { try {
                itemsToAdd.add(parser.getItem(item.getRef()));
            } catch (IOException e) { e.printStackTrace(); }});

        tableData.forEach(item -> itemDB.deleteItem(item, category));
        itemsToAdd.forEach(item -> itemDB.addItem(item, category));
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
                actualityDB.updateActualityInfo(category, now);
                parseProcesses.remove(category);
            } catch (IOException e) { e.printStackTrace(); }}).start();
        }
    }

    private void createTableIfNotExist(String category) {
        if (!tableInfo.containsKey(category)) {
            itemDB.createTable(category);
            itemDB.addItem(new SellingItem("Парсинг в процессе, перезагрузите страницу секунд через 30", "",
                    "", "", ""), category);
            tableInfo.put(category, new Date(0));
            actualityDB.createActualityInfo(category);
        }
    }

    //раз в полчаса проверяет актуальность данных в бд
    //не придумал, как вынести в отдельный класс, да и не уверен, что оно надо
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
