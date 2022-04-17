package com.parser;

import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public record AllItemsParser(HtmlBodyRequester body, ItemParser itemParser) {

    public List<SellingItem> fullParseItems(String url) throws IOException {
        Elements elements = body.get(url).getElementsByClass("ads-list-detail-item-thumb");
        ArrayList<SellingItem> items = new ArrayList<>();
        elements.forEach(element -> {
            try { items.add(itemParser.getItem(body.get("https://999.md/" + element.getElementsByTag("a").attr("href")))); }
            catch (IOException e) { e.printStackTrace(); }
        });
        return items;
    }

    public List<EasySellingItem> previewItems(String url) throws IOException, NullPointerException {
        Elements elements = body.get(url).getElementsByClass("ads-list-detail-item");
        ArrayList<EasySellingItem> items = new ArrayList<>();
        elements.forEach(element -> items.add(itemParser.getEasyItem(element)));
        return items;
    }

}
