package com.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class EasySellingItemParser {

    public List<EasySellingItem> getItems(String url) throws IOException, NullPointerException {
        Elements elements = Jsoup.connect(url).get().body().getElementsByClass("ads-list-detail-item");
        ArrayList<EasySellingItem> items = new ArrayList<>();
        elements.forEach(element -> items.add(getEasyItem(element)));
        return items;
    }

    private EasySellingItem getEasyItem(Element html) {
        return new EasySellingItem(
                nullCheck(html.getElementsByClass("ads-list-detail-item-price").first()).text(),
                nullCheck(html.getElementsByClass("ads-list-detail-item-title").first()).text(),
                html.getElementsByClass("ads-list-detail-item-thumb").first().getElementsByTag("a").first().attr("href")
        );
    }

    private Element nullCheck(Element element) {
        if (element == null) return new Element(Tag.valueOf("none"),"none");
        return element;
    }
}
