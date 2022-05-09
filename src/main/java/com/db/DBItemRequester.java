package com.db;

import com.db.repository.CategoryRepository;
import com.parser.EasySellingItemParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
public class DBItemRequester {
    Actualizer actualizer;
    CategoryRepository categoryRepository;
    EasySellingItemParser parser;
    @Autowired
    public DBItemRequester (Actualizer actualizer, EasySellingItemParser parser, CategoryRepository categoryRepository) {
        this.actualizer = actualizer;
        this.parser = parser;
        this.categoryRepository = categoryRepository;
    }

    public List getItems(String category, String mode) throws IOException {
        if (Objects.equals(mode, "full")) {
            actualizer.checkTableActuality(category);
            return categoryRepository.findFirstByCategory(category).getItems();
        }
        return parser.previewItems(category);
    }
}
