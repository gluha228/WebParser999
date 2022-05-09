package com.controllers;

import com.db.DBItemRequester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class ParseDataController {
    DBItemRequester dbItemRequester;
    @Autowired
    public ParseDataController(DBItemRequester dbItemRequester) {
        this.dbItemRequester = dbItemRequester;
    }

    @GetMapping ("/api/v1/fetch")
    public String directoryRequest(@RequestParam("section") String category, @RequestParam("mode") String mode, Model model) throws IOException {
        model.addAttribute("content", dbItemRequester.getItems("https://999.md" + category, mode));
        return "sectionData";
    }
}
