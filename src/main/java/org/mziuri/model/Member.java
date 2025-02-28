package org.mziuri.model;

public class Member {
    private final String id;
    private String name;
    private String author;
    private static String gmail;
    public Member(String name, String gmail, String id) {
        this.name = name;
        this.gmail = gmail;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static String getGmail() {
        return gmail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGmail(String gmail) {
        Member.gmail = gmail;
    }
}
