package com.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EasySellingItem {
    private String price = "no price";
    private String title = "no name";
    private String ref = "";

    @Override
    public String toString() {
        return this.price + " | " + this.title;
    }
}
