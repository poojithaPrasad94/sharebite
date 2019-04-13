/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.andrew.poojithp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Poojitha Prasad
 */
public class SharebiteModel {

    //Inner class to store menu sections
    private class Menu {

        int id;
        String name;

        public Menu(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * Helper method to read the JSON and get "name"
     *
     * @param input
     * @return name
     */
    public String readJson(String input) {
        JsonParser parser = new JsonParser();
        JsonObject o = parser.parse(input).getAsJsonObject();
        return o.get("name").getAsString();
    }

    /**
     * Updates the existing menu section
     *
     * @param idString
     * @param input
     * @return JSON reply to be sent to the client
     */
    public String postMenu(String idString, String input) {
        String name = this.readJson(input);
        int id = Integer.parseInt(idString);
        boolean executed;
        try (Connection connection = this.connect();) {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            String sql = "select * from menusection where id = " + id;
            ResultSet rs = statement.executeQuery(sql);
            if (!rs.isBeforeFirst()) {
                sql = "insert into menusection values(" + id + ", '" + name + "')";
                statement.executeUpdate(sql);
            } else {
                sql = "UPDATE menusection set name = \"" + name + "\" where ID=" + id + ";";
                statement.executeUpdate(sql);
            }
            executed = true;
        } catch (SQLException ex) {
            executed = false;
            Logger.getLogger(SharebiteModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", executed);
        jsonObject.add("MenuSection", this.getMenuArray(String.valueOf(id)));
        return jsonObject.toString();
    }

    /**
     * Inserts a new menu special in the DB
     *
     * @param input
     * @return
     */
    public String putMenu(String input) {
        String name = this.readJson(input);
        int count = -1;
        boolean executed;
        try (Connection connection = this.connect()) {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            String sql = "SELECT max(id) as count FROM menusection";
            ResultSet rs = statement.executeQuery(sql);
            count = rs.getInt("count");
            count++; //ID generated to store the new menu section

            sql = "INSERT INTO menusection(id,name) VALUES(" + count + ",\"" + name + "\")";
            statement.executeUpdate(sql);
            executed = true;
        } catch (SQLException ex) {
            executed = false;
            Logger.getLogger(SharebiteModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", executed);
        jsonObject.add("MenuSection", this.getMenuArray(String.valueOf(count)));
        return jsonObject.toString();
    }
    
    /**
     * Gets the menu based on the input
     * @param input
     * @return 
     */
    public String getMenu(String input) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("MenuSection", this.getMenuArray(input));
        return jsonObject.toString();
    }
    
    /**
     * Deletes the special from the menu based on the user input
     * @param input
     * @return 
     */
    public String deleteMenu(int input) {

        String sql = "DELETE FROM menusection WHERE id = ?";

        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // set the id parameter
            pstmt.setInt(1, input);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        JsonObject js = new JsonObject();
        js.addProperty("success", Boolean.TRUE);
        return js.toString();
    }

    /**
     * Helper method to create the menu section array
     * @param input
     * @return 
     */
    public JsonArray getMenuArray(String input) {
        List<Menu> temp = null;
        try (Connection connection = this.connect()) {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs;
            if (input.isEmpty()) {
                rs = statement.executeQuery("SELECT * FROM menusection");
            } else {
                rs = statement.executeQuery("SELECT * FROM menusection where id = " + input);
            }
            temp = new ArrayList<>();
            while (rs.next()) {
                temp.add(new Menu(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(SharebiteModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Creates the JSON array based of the menu section
        Gson gson = new Gson();
        String data = gson.toJson(temp);
        JsonArray jsonArray = new JsonArray();
        if (data != null && !data.isEmpty()) {
            jsonArray = new JsonParser().parse(data).getAsJsonArray();
        }
        return jsonArray;
    }
    
    /**
     * Creates the table called menu section
     */
    public void createDB() {

        try (Connection connection = this.connect();) {
            // create a database connection  
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists menusection");
            statement.executeUpdate("create table menusection (id integer, name string)");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    /**
     * Connects to the database
     * @return 
     */
    private Connection connect() {
        try {
            // load the sqlite-JDBC driver using the current class loader
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SharebiteModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        // SQLite connection string
        String desktopFolder = System.getProperty("user.home") + "\\Desktop\\sqlite";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + desktopFolder + "restaurant.db");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }
}
