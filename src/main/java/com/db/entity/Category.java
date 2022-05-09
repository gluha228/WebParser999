package com.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CATEGORY")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String category;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "category", cascade = CascadeType.ALL)
    private List<SellingItem> items = new ArrayList<>();
    private Date lastUpdate;

    public Category(String category) {
        this.category = category;
        this.lastUpdate = new Date(0);
        this.items = List.of(new SellingItem("Parse processing, please wait a minute", "", "", ""));
    }

}
