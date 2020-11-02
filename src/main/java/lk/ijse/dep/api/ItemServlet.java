package lk.ijse.dep.api;

import lk.ijse.dep.business.custom.ItemBO;
import lk.ijse.dep.dto.ItemDTO;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.NoSuchElementException;

@WebServlet(name = "ItemServlet", urlPatterns = "/items")
public class ItemServlet extends HttpServlet {
    private ItemBO itemBO;

    public static String getParameter(String queryString,String parameterName) throws UnsupportedEncodingException {
        if(queryString == null || parameterName == null || queryString.trim().isEmpty() || parameterName.trim().isEmpty()){
            return null;
        }
        String[] queryParams = queryString.split("&");
        for (String queryParam : queryParams) {
            if(queryParam.startsWith(parameterName) && queryParam.contains("=")){
                return URLDecoder.decode(queryParam.split("=")[1],"UTF-8");
            }
        }
        return null;
    }

    @Override
    public void init() throws ServletException {
        itemBO = ((AnnotationConfigApplicationContext)(getServletContext().getAttribute("ctx"))).getBean(ItemBO.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        try (PrintWriter out = resp.getWriter()){
            if(code == null){
                List<ItemDTO> allItems = itemBO.getAllItems();
                resp.setContentType("application/json");
                Jsonb jsonb = JsonbBuilder.create();
                String json = jsonb.toJson(allItems);
                out.println(json);
            }else{
                try {
                    ItemDTO item = itemBO.getItem(code);
                    out.println(item);
                } catch (NoSuchFieldException e) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND); //404
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        String description = req.getParameter("description");
        String quantityOnHand = req.getParameter("quantityOnHand");
        String unitPrice = req.getParameter("unitPrice");

        if(code == null || description == null || quantityOnHand == null || unitPrice == null || !code.matches("I\\d{3}") || description.trim().length() < 3 || Integer.parseInt(quantityOnHand) <= 0 || Double.parseDouble(unitPrice) <= 0){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST); //400
            return;
        }
        resp.setContentType("text/plain");

        try (PrintWriter out = resp.getWriter()){
            boolean exist = itemBO.isExist(code);
            if(exist){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);//400
            }else{
                itemBO.saveItem(code,description,Double.parseDouble(unitPrice),Integer.parseInt(quantityOnHand));
                resp.setStatus(HttpServletResponse.SC_CREATED); //201
                out.println("Item has been saved successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String queryString = req.getQueryString();
        String code = getParameter(queryString, "code");
        if(code == null){
            return;
        }
        if(!code.matches("I\\d{3}")){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST); //400
            return;
        }

        resp.setContentType("text/plain");

        try (PrintWriter out = resp.getWriter()){
            boolean exist = itemBO.isExist(code);
            if(exist){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST); //400
            }else{
                itemBO.deleteItem(code);
                out.println("Item has been deleted successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Reading the code from URL
        String queryString = req.getQueryString();
        if(queryString == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST); //400
            return;
        }
        String code = getParameter(queryString, "code");
        if(code == null){
            //Could not find code
            return;
        }
        //Reading description, quantityOnHand, unitPrice from body
        BufferedReader reader = req.getReader();
        String line = null;
        String requestBody = "";
        while((line = reader.readLine()) != null){
            requestBody += line;
        }

        String description = getParameter(queryString, "description");
        String quantityOnHand = getParameter(queryString, "quantityOnHand");
        String unitPrice = getParameter(queryString, "unitPrice");

        if(code == null || description == null || quantityOnHand == null || unitPrice == null || !code.matches("I\\d{3}") || description.trim().length() < 3 || Integer.parseInt(quantityOnHand) <= 0 || Double.parseDouble(unitPrice) <= 0){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST); //400
            return;
        }

        resp.setContentType("text/plain");

        try (PrintWriter out = resp.getWriter()){
            boolean exist = itemBO.isExist(code);
            if(exist){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);//400
            }else{
                itemBO.updateItem(description,Double.parseDouble(unitPrice),Integer.parseInt(quantityOnHand),code);
                out.println("Item has been updated successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
        }
    }
}
