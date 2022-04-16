package com.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SellingItem {
    private String price;
    private String title;
    private String phoneNumber;
    private String description;
    private String seller;

    //удивлен, что метод .toString используется таймлифом автоматически
    @Override
    public String toString() {
        return this.price + " | " + this.title + " | " + this.phoneNumber + " | " + this.seller;
    }
}
