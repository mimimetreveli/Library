package org.mziuri.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mziuri.model.Item;
import org.mziuri.model.Member;
import org.mziuri.service.DatabaseService;

import javax.management.StringValueExp;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

public class MemberServlet extends HttpServlet {
    private final DatabaseService databaseService;

    public MemberServlet(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        List<Member> members = (List<Member>) databaseService.getMembers();

        try (PrintWriter writer = resp.getWriter()) {
            writer.println("<html>");
            writer.println("<head><title>Library Members</title></head>");
            writer.println("<body>");
            writer.println("<h1><strong>Members</strong></h1>");
            if (members.isEmpty()) {
                writer.println("<p>No members available.</p>");
            } else {
                writer.println("<ul>");
                for (Member member : members) {
                    writer.println("<li>" + member.getName() + " - " + member.getGmail() + "</li>");
                }
                writer.println("</ul>");
            }
            writer.println("<h2><strong>Add a New Member</strong></h2>");
            writer.println("<form action='/members' method='POST'>");
            writer.println("Name: <input type='text' name='name' required><br>");
            writer.println("Gmail: <input type='email' name='gmail' required><br>");
            writer.println("<button type='submit'>Add Member</button>");
            writer.println("</form>");

            writer.println("</body>");
            writer.println("</html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String gmail = req.getParameter("gmail");

        if (name == null || name.isBlank() || gmail == null || gmail.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid name or gmail");
            return;
        }
        String id = generateRandomId();
        if (databaseService.getMemberByGmail(gmail) != null) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "A member with this Gmail already exists");
            return;
        }
        Member member = new Member(name, gmail, id);
        databaseService.addMember(member);
        resp.sendRedirect("/members");
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String memberid = req.getPathInfo().substring(1);
        int id;

        try {
            id = Integer.parseInt(memberid);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid id");
            return;
        }

        String name = req.getParameter("name");
        String gmail = req.getParameter("gmail");

        if (name == null || name.isBlank() || gmail == null || gmail.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid name or gmail");
            return;
        }

        Member member = databaseService.getMemberById(String.valueOf(id));
        if (member == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "member not found");
            return;
        }
        member.setName(name);
        member.setGmail(gmail);
        databaseService.updateMember(member);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("Book updated successfully");
    }
    private String generateRandomId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String memberid = req.getPathInfo().substring(1);
        int id;

        try {
            id = Integer.parseInt(memberid);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid id");
            return;
        }

        Member member = databaseService.getMemberById(String.valueOf(id));
        if (member == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book with this code not found");
            return;
        }
        databaseService.deleteMemberById(String.valueOf(id));
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}