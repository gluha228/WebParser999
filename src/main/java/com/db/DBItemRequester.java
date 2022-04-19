package com.db;

import com.db.sql.SellingItemCRUD;
import com.parser.AllItemsParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
public class DBItemRequester {
    Actualizer actualizator;
    SellingItemCRUD crud;
    AllItemsParser parser;
    @Autowired
    public DBItemRequester (Actualizer actualizator, SellingItemCRUD crud, AllItemsParser parser) {
        this.actualizator = actualizator;
        this.parser = parser;
        this.crud = crud;
    }

    public List getItems(String category, String mode) throws IOException {
        if (Objects.equals(mode, "full")) {
            actualizator.checkTableActuality(category);
            return crud.readItems(category);
        }
        return parser.previewItems(category);
    }
}
