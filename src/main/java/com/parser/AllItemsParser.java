package com.parser;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AllItemsParser {
    @Autowired
    private HtmlBodyRequester body;
    @Autowired
    private ItemParser itemParser;

    public List<SellingItem> parseItems (String url) throws IOException {
        System.out.println("//////////////////////////"+ url);
        Element html = body.get(url);
        ArrayList<SellingItem> items = new ArrayList<>();
        Elements elements = html.getElementsByClass("ads-list-photo-item-animated-link");
        elements.forEach(element -> {
            try {
                System.out.println("https://999.md/" + element.attr("href"));
                items.add(itemParser.getItem(body.get("https://999.md/" + element.attr("href"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return items;
    }
}
