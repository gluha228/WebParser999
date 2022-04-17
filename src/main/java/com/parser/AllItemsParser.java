package com.parser;

import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public record AllItemsParser(HtmlBodyRequester body, ItemParser itemParser) {

    public List<EasySellingItem> previewItems(String url) throws IOException, NullPointerException {
        Elements elements = body.get(url).getElementsByClass("ads-list-detail-item");
        ArrayList<EasySellingItem> items = new ArrayList<>();
        elements.forEach(element -> items.add(itemParser.getEasyItem(element)));
        return items;
    }

    public SellingItem getItem(String url) throws IOException {
        return itemParser.getItem(body.get("https://999.md/" + url));
    }

}
