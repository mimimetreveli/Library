package org.mziuri;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.mziuri.service.DatabaseService;
import org.mziuri.servlet.BookServlet;
import org.mziuri.servlet.BorrowingServlet;
import org.mziuri.servlet.MemberServlet;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
public class Main {
            public static void main(String[] args) throws LifecycleException {
                String url = "jdbc:postgresql://localhost:5432/your_database";
                String user = "user";
                String password = "password";

                try (Connection conn = DriverManager.getConnection(url, user, password);
                     Statement stmt = conn.createStatement()) {
                    String createBooksTable = """
                CREATE TABLE IF NOT EXISTS Books (
                    code SERIAL PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    author VARCHAR(255) NOT NULL
                )""";
                    stmt.execute(createBooksTable);
                    String createMembersTable = """
                CREATE TABLE IF NOT EXISTS Members (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    email VARCHAR(255) UNIQUE NOT NULL,
                    join_date DATE NOT NULL
                )""";
                    stmt.execute(createMembersTable);
                    String createBorrowingsTable = """
                CREATE TABLE IF NOT EXISTS Borrowings (
                    book_code INT NOT NULL,
                    member_id INT NOT NULL,
                    borrow_date DATE NOT NULL,
                    return_date DATE,
                    PRIMARY KEY (book_code, member_id, borrow_date),
                    FOREIGN KEY (book_code) REFERENCES Books(code) ON DELETE CASCADE,
                    FOREIGN KEY (member_id) REFERENCES Members(id) ON DELETE CASCADE
                )""";
                    stmt.execute(createBorrowingsTable);

                    System.out.println("Tables created successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("temp");
        tomcat.setPort(8080);
        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();
        Context context = tomcat.addContext(contextPath, docBase);
        Tomcat.addServlet(context, "BookServlet", new BookServlet(new DatabaseService()));
        Tomcat.addServlet(context, "BorrowingServlet", new BorrowingServlet(new DatabaseService()));
        Tomcat.addServlet(context, "MemberServlet", new MemberServlet(new DatabaseService()));
        context.addServletMappingDecoded("/books", "BookServlet");
        context.addServletMappingDecoded("/borrowings", "BorrowingServlet");
        context.addServletMappingDecoded("/members", "MemberServlet");
        tomcat.start();
        tomcat.getConnector();
        tomcat.getServer().await();
    }

}