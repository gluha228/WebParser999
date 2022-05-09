package com.db;

import com.db.entity.Category;
import com.db.repository.CategoryRepository;
import com.db.repository.SellingItemRepository;
import com.parser.EasySellingItemParser;
import com.db.entity.SellingItem;
import com.parser.EasySellingItem;
import com.parser.ItemParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class Actualizer {
    private final SellingItemRepository sellingItemRepository;
    private final CategoryRepository categoryRepository;
    private final ItemParser itemParser;
    private final List<String> parseProcesses = new ArrayList<>();
    private final EasySellingItemParser easySellingItemParser;
    private final Logger logger = LoggerFactory.getLogger(Actualizer.class);

    @Autowired
    public Actualizer(EasySellingItemParser parser, SellingItemRepository sellingItemRepository,
                      ItemParser itemParser, CategoryRepository categoryRepository) {
        this.easySellingItemParser = parser;
        this.sellingItemRepository = sellingItemRepository;
        this.itemParser = itemParser;
        this.categoryRepository = categoryRepository;
        sideUpdate();
    }

    //довольно долго, но минимизированы перезаписи в дб
    //используется частичный парсинг(только общей страницы), он дает только цену и название
    private void updateTableActuality(String category) throws IOException {
        logger.info("starting update of " + category);
        List<EasySellingItem> parseData = easySellingItemParser.getItems(category);
        Category categoryObject = categoryRepository.findFirstByCategory(category);
        List<SellingItem> tableData = categoryObject.getItems();//itemDB.readItems(category);
        //вычитание пересечений множеств
        for (int i = 0; i < parseData.size(); i++) {
            for (int j = 0; j < tableData.size(); j++) {
                if (Objects.equals(tableData.get(j).getPrice(), parseData.get(i).getPrice()) &&
                        Objects.equals(tableData.get(j).getTitle(), parseData.get(i).getTitle())) {
                    parseData.remove(i);
                    tableData.remove(j);
                    i--;
                    break;
                }
            }
        }
        List<SellingItem> itemsToAdd = new ArrayList<>();
        parseData.forEach(item -> { try {
            itemsToAdd.add(itemParser.getItem("https://999.md" + item.getRef()));
            } catch (IOException e) { e.printStackTrace(); }
        });
        itemsToAdd.forEach(item -> item.setCategory(categoryObject));
        sellingItemRepository.deleteAll(tableData);
        sellingItemRepository.saveAll(itemsToAdd);
        logger.info("ended update of " + category);
    }

    public void checkTableActuality(String category) {
        if (parseProcesses.contains(category)) return;
        if (!categoryRepository.existsByCategory(category)) categoryRepository.save(new Category(category));
        Date now = new Date(System.currentTimeMillis());
        int updateTime = 999999999; //оставлю 999 в покое
        if (TimeUnit.MINUTES.convert(now.getTime() -
                categoryRepository.findFirstByCategory(category).getLastUpdate().getTime(), TimeUnit.MILLISECONDS) > updateTime) {
            new Thread(() -> { try {
                parseProcesses.add(category);
                updateTableActuality(category);
                categoryRepository.setLastUpdateByCategory(now, category);
                parseProcesses.remove(category);
            } catch (IOException e) { e.printStackTrace(); }}).start();
        }
    }

    public void sideUpdate() {
        new Thread(() -> {
            while (true) {
                categoryRepository.findAll().forEach(category -> checkTableActuality(category.getCategory()));
                try { Thread.sleep(1000*60*30); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }).start();
    }

}
