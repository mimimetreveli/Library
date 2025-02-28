package org.mziuri.service;

import org.mziuri.model.Item;
import org.mziuri.model.Member;

import java.time.Instant;
import java.util.*;

public class DatabaseService {
    private final Map<String, Item> items;
    private final Set<String> borrowedItems;
    private final List<Member> members;
    private final Map<String, List<Instant>> borrowHistory;
    private final Map<String, String> bookToMemberMap;
    public DatabaseService() {
        this.items = new HashMap<>();
        this.borrowedItems = new HashSet<>();
        this.members = new ArrayList<>();
        this.borrowHistory = new HashMap<>();
        this.bookToMemberMap = new HashMap<>();
    }
    public void addItem(Item item) {
        items.put(String.valueOf(item.getCode()), item);
    }
    public Item getItem(String code) {
        return items.get(code);
    }
    public Collection<Item> getItems() {
        return items.values();
    }
    public void deleteItem(String code) {
        items.remove(code);
        borrowedItems.remove(code);
        borrowHistory.remove(code);
        bookToMemberMap.remove(code);
    }
    public void addMember(Member member) {
        members.add(member);
    }
    public List<Member> getMembers() {
        return new ArrayList<>(members);
    }
    public Member getMemberByGmail(String gmail) {
        for (Member member : members) {
            if (member.getGmail().equalsIgnoreCase(gmail)) {
                return member;
            }
        }
        return null;
    }
    public boolean isBookBorrowed(String bookCode) {
        return borrowedItems.contains(bookCode);
    }
    public void borrowBook(String bookCode, String memberId) {
        if (isBookBorrowed(bookCode)) {
            throw new IllegalArgumentException("Book is already borrowed.");
        }
        borrowedItems.add(bookCode);
        bookToMemberMap.put(bookCode, memberId);
        borrowHistory.putIfAbsent(bookCode, new ArrayList<>());
        borrowHistory.get(bookCode).add(Instant.now());
    }
    public void returnBook(String bookCode) {
        if (!isBookBorrowed(bookCode)) {
            throw new IllegalArgumentException("Book is not borrowed.");
        }
        borrowedItems.remove(bookCode);
        bookToMemberMap.remove(bookCode);
    }
    public List<Instant> getBorrowHistory(String bookCode) {
        return borrowHistory.getOrDefault(bookCode, new ArrayList<>());
    }
    public String getBookBorrower(String bookCode) {
        return bookToMemberMap.get(bookCode);
    }
    public List<String> getAllBorrowedBooksWithDetails() {
        List<String> borrowedBooksDetails = new ArrayList<>();
        for (String bookCode : borrowedItems) {
            String memberId = getBookBorrower(bookCode);
            List<Instant> history = getBorrowHistory(bookCode);
            for (Instant timestamp : history) {
                borrowedBooksDetails.add("Book: " + bookCode + ", Borrowed by: " + memberId + ", At: " + timestamp);
            }
        }
        return borrowedBooksDetails;
    }

    public Member getMemberById(String id) {
        for (Member member : members) {
            if (member.getId().equals(id)) {
                return member;
            }
        }
        return null;
    }
    public void updateMember(Member updatedMember) {
        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            if (member.getId().equals(updatedMember.getId())) {
                members.set(i, updatedMember);
                return;
            }
        }
        throw new IllegalArgumentException("Member not found");
    }
    public void deleteMemberById(String id) {
        members.removeIf(member -> member.getId().equals(id));
    }
    public void updateItem(Item updatedItem) {
        String code = String.valueOf(updatedItem.getCode());
        if (items.containsKey(code)) {
            items.put(code, updatedItem);
        } else {
            throw new IllegalArgumentException("Book not found");
        }
    }
    public List<Item> getBorrowedBooks() {
        List<Item> borrowedBooks = new ArrayList<>();
        for (String bookCode : borrowedItems) {
            borrowedBooks.add(items.get(bookCode));
        }
        return borrowedBooks;
    }
}