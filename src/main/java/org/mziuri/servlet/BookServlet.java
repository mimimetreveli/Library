package org.mziuri.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mziuri.model.Item;
import org.mziuri.service.DatabaseService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class BookServlet extends HttpServlet {
    private final DatabaseService databaseService;

    public BookServlet(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        List<Item> books = (List<Item>) databaseService.getItems();

        try (PrintWriter writer = resp.getWriter()) {
            writer.println("<html>");
            writer.println("<head><title>Book List</title></head>");
            writer.println("<body>");
            writer.println("<h1>Books</h1>");

            if (books.isEmpty()) {
                writer.println("<p>No books available.</p>");
            } else {
                writer.println("<ul>");
                for (Item book : books) {
                    writer.println("<li>" + book.getName() + " by " + book.getAuthor() + " - Code: <strong>" + book.getCode() + "</strong></li>");
                }
                writer.println("</ul>");
            }

            writer.println("<h2>Add a New Book</h2>");
            writer.println("<form action='/books' method='POST'>");
            writer.println("Title: <input type='text' name='title' required><br>");
            writer.println("Author: <input type='text' name='author' required><br>");
            writer.println("Code: <input type='text' name='code' required><br>");
            writer.println("<button type='submit'>Add Book</button>");
            writer.println("</form>");

            writer.println("</body>");
            writer.println("</html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String title = req.getParameter("title");
        String author = req.getParameter("author");
        String code = req.getParameter("code");

        if (title == null || title.isBlank() ||
                author == null || author.isBlank() ||
                code == null || code.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid book title, author, or code");
            return;
        }

        int bookCode;
        try {
            bookCode = Integer.parseInt(code);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Code must be a valid number");
            return;
        }
        if (databaseService.getItem(code) != null) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "A book with this code already exists");
            return;
        }
        Item book = new Item(title, author, bookCode);
        databaseService.addItem(book);
        resp.sendRedirect("/books");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String bookCodeParam = req.getPathInfo().substring(1); // Extract the bookCode from the URL
        int bookCode;

        try {
            bookCode = Integer.parseInt(bookCodeParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid book code");
            return;
        }

        String title = req.getParameter("title");
        String author = req.getParameter("author");

        if (title == null || title.isBlank() || author == null || author.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid book title or author");
            return;
        }

        Item existingBook = databaseService.getItem(String.valueOf(bookCode));
        if (existingBook == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book with this code not found");
            return;
        }

        existingBook.setName(title);
        existingBook.setAuthor(author);

        databaseService.updateItem(existingBook);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("Book updated successfully");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String bookCodeParam = req.getPathInfo().substring(1);
        int bookCode;

        try {
            bookCode = Integer.parseInt(bookCodeParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid book code");
            return;
        }

        Item book = databaseService.getItem(String.valueOf(bookCode));
        if (book == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book with this code not found");
            return;
        }

        databaseService.deleteItem(String.valueOf(bookCode));
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
