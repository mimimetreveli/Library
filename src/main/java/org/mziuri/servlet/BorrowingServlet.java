package org.mziuri.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mziuri.model.Item;
import org.mziuri.model.Member;
import org.mziuri.service.DatabaseService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class BorrowingServlet extends HttpServlet {
    private final DatabaseService databaseService;

    public BorrowingServlet(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        List<Item> borrowedBooks = databaseService.getBorrowedBooks();

        try (PrintWriter writer = resp.getWriter()) {
            writer.println("<html>");
            writer.println("<head><title>Borrowed Books</title></head>");
            writer.println("<body>");
            writer.println("<h1><strong>Borrowed Books</strong></h1>");

            if (borrowedBooks.isEmpty()) {
                writer.println("<p>No borrowed books.</p>");
            } else {
                writer.println("<ul>");
                for (Item book : borrowedBooks) {
                    writer.println("<li>" + book.getName() + " by " + book.getAuthor() + " - Borrowed by: " + book.getName() + "</li>");
                }
                writer.println("</ul>");
            }
            writer.println("<h2><strong>Borrow a Book</strong></h2>");
            writer.println("<form action='/borrow' method='POST'>");
            writer.println("Book Code: <input type='text' name='bookCode' required><br>");
            writer.println("Member ID: <input type='text' name='memberId' required><br>");
            writer.println("<button type='submit' name='action' value='borrow'>Borrow Book</button>");
            writer.println("</form>");
            writer.println("<h2><strong>Return a Book</strong></h2>");
            writer.println("<form action='/borrow' method='POST'>");
            writer.println("Book Code: <input type='text' name='bookCode' required><br>");
            writer.println("<button type='submit' name='action' value='return'>Return Book</button>");
            writer.println("</form>");

            writer.println("</body>");
            writer.println("</html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String bookCode = req.getParameter("bookCode");
        String memberId = req.getParameter("memberId");
        String action = req.getParameter("action");
        if (bookCode == null || bookCode.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid book code");
            return;
        }
        if ("borrow".equals(action)) {
            if (memberId == null || memberId.isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid member ID");
                return;
            }
            Item book = databaseService.getItem(bookCode);
            Member member = databaseService.getMemberById(memberId);

            if (book == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
                return;
            }

            if (member == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Member not found");
                return;
            }
            if (databaseService.isBookBorrowed(bookCode)) {
                resp.sendError(HttpServletResponse.SC_CONFLICT, "The book is already borrowed");
                return;
            }
            databaseService.borrowBook(bookCode, memberId);
            resp.sendRedirect("/borrow");
        }
        else if ("return".equals(action)) {
            Item book = databaseService.getItem(bookCode);
            if (book == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
                return;
            }
            if (!databaseService.isBookBorrowed(bookCode)) {
                resp.sendError(HttpServletResponse.SC_CONFLICT, "The book is not borrowed");
                return;
            }
            databaseService.returnBook(bookCode);
            resp.sendRedirect("/borrow");
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }
}