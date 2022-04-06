package com;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
//возвращает хтмл код страницы
@Component
public class HtmlBodyRequester {
    public Element get(String string) throws IOException {
        Document document = Jsoup.connect(string).get();
        return document.body();
    }
}
