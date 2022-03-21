import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public class HtmlStringRequester {
    public String get(String string) throws IOException {
        Document document = Jsoup.connect(string).get();
        return document.body().toString();
    }
}
