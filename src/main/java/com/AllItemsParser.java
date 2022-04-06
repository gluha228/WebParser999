package com;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class AllItemsParser {
    @Autowired
    private HtmlBodyRequester body;
    @Autowired
    private ItemParser itemParser;

    public ArrayList<SellingItem> parseItems (String url) throws IOException {
        Element html = body.get(url);
        ArrayList<SellingItem> items = new ArrayList<>();
        Elements elements = html.getElementsByClass("ads-list-photo-item-animated-link");
        elements.forEach(element -> {
            try {
                //System.out.println(element.attr("href"));
                items.add(itemParser.getItem(body.get("https://999.md/" + element.attr("href"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return items;
    }
}
