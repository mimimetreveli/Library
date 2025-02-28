package org.mziuri.model;

import java.time.Instant;
import java.time.LocalDate;

public class BorrowedBook {
    private final Item book;
    private final Member member;
    private final Instant borrowedAt;
    public BorrowedBook(Item book, Member member, Instant borrowedAt) {
        this.book = book;
        this.member = member;
        this.borrowedAt = borrowedAt;
    }
    public void borrowBook(String bookCode, String memberId, LocalDate borrowDate) {
        String query = "SELECT return_date FROM borrowed_books WHERE book_code = ? ORDER BY borrow_date DESC LIMIT 1";
        String insertQuery = "INSERT INTO borrowed_books (book_code, member_id, borrow_date) VALUES (?, ?, ?)";
    }
    public Item getBook() {
        return book;
    }

    public Member getMember() {
        return member;
    }

    public Instant getBorrowedAt() {
        return borrowedAt;
    }
    @Override
    public String toString() {
        return "Book: " + book.getName() + ", Borrowed by: " + member.getName() + " at " + borrowedAt;
    }
}