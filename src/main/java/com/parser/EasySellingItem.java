package com.parser;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class EasySellingItem {
    private String price = "no price";
    private String title = "no name";

    @Override
    public String toString() {
        return this.price + " | " + this.title;
    }
}
