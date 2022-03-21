import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
//возвращает хтмл код страницы
public class HtmlBodyRequester {
    public Element get(String string) throws IOException {
        Document document = Jsoup.connect(string).get();
        return document.body();
    }
}
