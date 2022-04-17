package com.parser;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.springframework.stereotype.Component;

@Component
public class ItemParser {
    public SellingItem getItem(Element html) {
        return new SellingItem(
                nullCheck(html.getElementsByClass("adPage__content__price-feature__prices__price__value").first()).text(),
                html.getElementsByTag("h1").text(),
                nullCheck(html.getElementsByClass("adPage__content__phone").first().getElementsByTag("a").first()).attr("href"),
                html.getElementsByClass("adPage__content__description").text(),
                html.getElementsByClass("adPage__aside__stats__owner__login").text()
        );
    }
    public EasySellingItem getEasyItem(Element html) {
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
