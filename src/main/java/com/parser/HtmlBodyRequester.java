package com.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class HtmlBodyRequester {
    public Element get(String url) throws IOException {
        return Jsoup.connect(url).get().body();
    }
}
