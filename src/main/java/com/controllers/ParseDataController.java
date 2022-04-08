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
    AllItemsParser itemsParser;
    @Autowired
    public ParseDataController(AllItemsParser itemsParser) {
        this.itemsParser = itemsParser;
    }

    @GetMapping ("/api/v1/fetch")
    public String directoryRequest(@RequestParam("section") String section, @RequestParam("mode") String mode, Model model) throws IOException {
        List items = itemsParser.parseItems("https://999.md" + section, mode);
        model.addAttribute("content", items);
        return "sectionData";
    }
}
