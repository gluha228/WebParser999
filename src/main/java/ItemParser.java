import org.jsoup.nodes.Element;

public class ItemParser {
    public SellingItem getAllItems(Element html) {
        return new SellingItem(
                html.getElementsByClass("adPage__content__price-feature__prices__price__value").first().text(),
                html.getElementsByTag("h1").text(),
                html.getElementsByClass("adPage__content__phone").first().getElementsByTag("a").first().attr("href"),
                html.getElementsByClass("adPage__content__description").text(),
                html.getElementsByClass("adPage__aside__stats__owner__login").text()
        );
    }
}
