package com.controllers;

import com.parser.AllItemsParser;
import com.parser.SellingItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
public class ParseDataController {
    @Autowired
    AllItemsParser itemsParser;

    @GetMapping ("/api/v1/fetch")
    public String directoryRequest(@RequestParam("section") String section, Model model) throws IOException {
        System.out.println(section);
        List<SellingItem> items = itemsParser.parseItems("https://999.md" + section);
        model.addAttribute("content", items);
        return "sectionData";
    }

}
