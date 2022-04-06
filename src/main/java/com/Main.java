package com;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String args[]) throws IOException {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(SpringConfig.class);
        AllItemsParser parser = context.getBean(AllItemsParser.class);
        ArrayList<SellingItem> items = parser.parseItems("https://999.md/ru/list/clothes-and-shoes/watches");
        items.forEach(item -> item.publish());


    }

}
