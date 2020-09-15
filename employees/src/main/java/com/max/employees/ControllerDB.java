package com.max.employees;

import com.vaadin.ui.TextField;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControllerDB {

    private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private final String DB_URL = "jdbc:mysql://localhost:3306/companies?useUnicode=true&serverTimezone=UTC";
    private final String USER = "root";
    private final String PASS = "root";

    private Connection conn = null;
    private Statement stmt = null;

    public List<Company> loadCompanies() {
        List<Company> list = new ArrayList<>();

        ResultSet rs = openConnection("SELECT id, name FROM companies");

        try {
            while (rs.next()) {
                list.add(new Company(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if (!closeConnection())
            return null;
        return list;
    }


    public List<Employee> loadEmployees(int company_id) {
        List<Employee> list = new ArrayList<>();

        ResultSet rs = openConnection("SELECT id, firstName, lastName, date, position, companyId " +
                "FROM employees WHERE companyId=" + company_id);

        try {
            while (rs.next()) {
                list.add(new Employee(rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("date"),
                        rs.getString("position"),
                        rs.getInt("companyId")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if (!closeConnection())
            return null;

        return list;
    }


    public boolean updateEmployee(String fName, String lName, String date, String pos, int id) {
        String request = "UPDATE employees SET firstName=?, lastName=?, date=?, position=? WHERE id=?;";

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            PreparedStatement stmt = conn.prepareStatement(request);
            stmt.setString(1, fName);
            stmt.setString(2, lName);
            stmt.setString(3, date);
            stmt.setString(4, pos);
            stmt.setInt(5, id);

            stmt.executeUpdate();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }


    public boolean addEmployee(List<TextField> list, int companyId) {
        String request = "INSERT INTO employees(id,firstName,lastName,date,position,companyId) VALUES(?,?,?,?,?,?);";

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            PreparedStatement stmt = conn.prepareStatement(request);
            for (int i = 0; i < list.size(); i++)
                stmt.setString(i+1, list.get(i).getValue());

            stmt.setInt(6, companyId);

            stmt.executeUpdate();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return true;
    }


    private ResultSet openConnection(String request) {
        ResultSet result = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            result = stmt.executeQuery(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }


    private boolean closeConnection() {
        try {
            stmt.close();
            conn.close();
        } catch (Exception se) {
            se.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException ignored) {
                return false;
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
                return false;
            }
        }

        return true;
    }

}
