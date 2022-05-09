package com.db.entity;

import lombok.*;

import javax.persistence.*;


@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "SELLING_ITEM")
public class SellingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private String price;
    private String title;
    private String phoneNumber;
    private String seller;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "category")
    private Category category;

    public SellingItem(String price, String title, String phoneNumber, String seller) {
        this.price = price;
        this.title = title;
        this.phoneNumber = phoneNumber;
        this.seller = seller;
    }

    //удивлен, что метод .toString используется таймлифом автоматически
    @Override
    public String toString() {
        return this.price + " | " + this.title + " | " + this.phoneNumber + " | " + this.seller;
    }
}
