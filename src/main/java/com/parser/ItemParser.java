package com.parser;
import com.db.entity.SellingItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ItemParser {
    public SellingItem getItem(String url) throws IOException {
        Element html = Jsoup.connect(url).get().body();
        return new SellingItem(
                nullCheck(html.getElementsByClass("adPage__content__price-feature__prices__price__value").first()).text(),
                html.getElementsByTag("h1").text(),
                nullCheck(html.getElementsByClass("adPage__content__phone").first().getElementsByTag("a").first()).attr("href"),
                html.getElementsByClass("adPage__aside__stats__owner__login").text()
        );
    }

    private Element nullCheck(Element element) {
        if (element == null) return new Element(Tag.valueOf("none"),"none");
        return element;
    }
}
