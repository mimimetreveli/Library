
package org.mziuri.model;

public class Item {
    private String name;
    private String author;
    private int code;

    public Item(String name, String author, int code) {
        this.name = name;
        this.author = author;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public int getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
