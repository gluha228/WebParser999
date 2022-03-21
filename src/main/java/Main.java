import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String args[]) throws IOException {
        AllItemsParser allItemsParser = new AllItemsParser();
        HtmlBodyRequester htmlStringRequester = new HtmlBodyRequester();
        ItemParser itemParser = new ItemParser();
        ArrayList<String> urls = allItemsParser.getAllItems(htmlStringRequester.get("https://999.md/ru/list/clothes-and-shoes/watches"));

        urls.forEach(url -> {
            try {
                itemParser.getAllItems(htmlStringRequester.get("https://999.md/" + url)).publish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
