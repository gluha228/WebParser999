package com.parser;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class ItemParser {
    public SellingItem getItem(Element html) {
        return new SellingItem(
                getPrice(html.getElementsByClass("adPage__content__price-feature__prices__price__value").first()),
                html.getElementsByTag("h1").text(),
                getPhone(html.getElementsByClass("adPage__content__phone").first().getElementsByTag("a").first()),
                html.getElementsByClass("adPage__content__description").text(),
                html.getElementsByClass("adPage__aside__stats__owner__login").text()
        );
    }
    //на объявления с неуказанным номером или ценой всё ломалось
    private String getPhone(Element element) {
        if (element == null) return "no phone";
        return element.attr("href");
    }
    private String getPrice(Element element) {
        if (element == null) return "no price";
        return element.text();
    }
}
