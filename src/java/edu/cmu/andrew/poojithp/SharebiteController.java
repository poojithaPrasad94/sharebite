package edu.cmu.andrew.poojithp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class to handle API requests.
 *
 * @author Poojitha Prasad
 */
@WebServlet(name = "Sharebite", urlPatterns = {"/menusection/*"})
public class SharebiteController extends HttpServlet {

    // Global variable to connect with Model class
    SharebiteModel sharebiteModel;

    /**
     * Initialize the model's object and create SQLite DB
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        sharebiteModel = new SharebiteModel();
        sharebiteModel.createDB();
    }
    
    /**
     * Handles the GET request and sends the menu section in response
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("Console: doGET visited");
        String id = "";
        if ((request.getPathInfo()) != null) {
            id = (request.getPathInfo()).substring(1);
        }
        //Gets the menu from database
        String getResponse = sharebiteModel.getMenu(id);
        response.setStatus(200);
        response.setContentType("text/plain;charset=UTF-8");

        // return the value from a GET request
        PrintWriter out = response.getWriter();
        out.println(getResponse);
    }

    /**
     * Handles the delete request
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Console: doDELETE visited");
        String id = "";
        if ((request.getPathInfo()) != null) {
            id = (request.getPathInfo()).substring(1);
        }
        String j = sharebiteModel.deleteMenu(Integer.parseInt(id));

        response.setStatus(200);
        response.setContentType("text/plain;charset=UTF-8");

        // return the value from a DELETE request
        PrintWriter out = response.getWriter();
        out.println(j);
    }
    
    /**
     * Handles the PUT request and inserts in the database
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Console: doPUT visited");
        String data = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String str;
            while ((str = br.readLine()) != null) {
                data += str;
            }
        } catch (IOException e) {
            System.out.println("Error");
        }
        String result = sharebiteModel.putMenu(data);

        response.setStatus(200);
        response.setContentType("text/plain;charset=UTF-8");

        // return the value from a PUT request
        PrintWriter out = response.getWriter();
        out.println(result);
    }

    /**
     * Handles the POST request and updates the database
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Console: doPOST visited");
        String id = "";
        if ((request.getPathInfo()) != null) {
            id = (request.getPathInfo()).substring(1);
        }
        String data = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String str;
            while ((str = br.readLine()) != null) {
                data += str;
            }
        } catch (IOException e) {
            System.out.println("Error");
        }
        response.setContentType("text/plain;charset=UTF-8");
        String result = sharebiteModel.postMenu(id, data);
        PrintWriter out = response.getWriter();
        out.println(result);
    }

}
