import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllItemsParser {
    public ArrayList<String> getAllItems(Element html) {
        Elements elements = html.getElementsByClass("ads-list-photo-item-animated-link");
        ArrayList<String> urls = new ArrayList<>();
        elements.forEach(element ->  urls.add(getUrl(element)));
        return urls;
    }

    public String getUrl(Element url) {
        String urlString = url.toString();
        return urlString.substring(urlString.indexOf("/"), urlString.indexOf("\">"));
    }

}
