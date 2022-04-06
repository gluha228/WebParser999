package com;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SellingItem {
    private String price;
    private String title;
    private String phoneNumber;
    private String description;
    private String seller;
    public void publish() {
        System.out.println(price + " | " + title + " | " + phoneNumber + " | " + seller);
    }
}
