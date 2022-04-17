package com;

import com.db.DBRequester;
import com.parser.AllItemsParser;
import com.parser.HtmlBodyRequester;
import com.parser.ItemParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ServingWebContentApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(ServingWebContentApplication.class, args);
        }


}
