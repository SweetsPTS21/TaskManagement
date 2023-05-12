package com.example.demoktra2.model;

import java.io.Serializable;

public class Item implements Serializable {
    private int id;
    private String title,title2, date,category,category2;

    public Item() {

    }

    public Item(int id, String title, String title2, String date, String category, String category2) {
        this.id = id;
        this.title = title;

        this.title2 = title2;
        this.date = date;
        this.category = category;
        this.category2 = category2;
    }

    public Item(String title, String title2, String date, String category, String category2) {
        this.title = title;

        this.title2 = title2;
        this.date = date;
        this.category = category;
        this.category2 = category2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }
}
