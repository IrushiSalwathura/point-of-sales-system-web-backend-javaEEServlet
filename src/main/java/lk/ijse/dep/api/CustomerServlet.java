package lk.ijse.dep.api;

import lk.ijse.dep.business.custom.CustomerBO;
import lk.ijse.dep.dto.CustomerDTO;
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
@WebServlet(name = "CustomerServlet", urlPatterns = "/customers")
public class CustomerServlet extends HttpServlet {
    private CustomerBO customerBO;

   public static String getParameter(String queryString, String parameterName) throws UnsupportedEncodingException {
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
        customerBO = ((AnnotationConfigApplicationContext) (getServletContext().getAttribute("ctx"))).getBean(CustomerBO.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        try (PrintWriter out = resp.getWriter()){
            if(id == null){
                List<CustomerDTO> allCustomers = customerBO.getAllCustomers();
                resp.setContentType("application/json");
                //Create Jsonb and serialize
                Jsonb jsonb = JsonbBuilder.create();
                String json = jsonb.toJson(allCustomers);
                out.println(json);
            }else{
                try {
                    CustomerDTO customer = customerBO.getCustomer(id);
                    out.println(customer);
                } catch (NoSuchElementException e) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);//404
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String address = req.getParameter("address");

        if(id == null || name == null || address == null || !id.matches("C\\d{3}") || name.trim().length() < 3 || address.trim().length() < 3){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST); //400
            return;
        }

        resp.setContentType("text/plain");

        try (PrintWriter out = resp.getWriter()){
            boolean exist = customerBO.isExist(id);
            if(exist){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST); //400
            }else{
                customerBO.saveCustomer(id,name,address);
                resp.setStatus(HttpServletResponse.SC_CREATED); //201
                out.println("Customer has been saved successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String queryString = req.getQueryString();
        String id = getParameter(queryString, "id");
        if(id == null){
            return;
        }

        if(!id.matches("C\\d{3}")){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST); //400
            return;
        }

        resp.setContentType("text/plain");
        try (PrintWriter out = resp.getWriter()){
            boolean exist = customerBO.isExist(id);
            if(exist){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST); //400
            }else{
                customerBO.deleteCustomer(id);
                out.println("Customer has been deleted successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       //Reading the id from the URL
       String queryString = req.getQueryString();
       if(queryString == null){
           resp.sendError(HttpServletResponse.SC_BAD_REQUEST); //400
           return;
       }
       String id = getParameter(queryString, "id");
       if(id == null){
           //Could not find an id
           return;
       }
        //Reading name and address from body
        BufferedReader reader = req.getReader();
        String line = null;
        String requestBody = "";
        while((line = reader.readLine()) != null){
            requestBody += line;
        }
        String name = getParameter(queryString, "name");
        String address = getParameter(queryString, "address");

        if(id == null || name == null || address == null || !id.matches("C\\d{3}") || name.trim().length() < 3 || address.trim().length() < 3 ){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST); //400
            return;
        }
        resp.setContentType("text/plain");
        try (PrintWriter out = resp.getWriter()){
            boolean exist = customerBO.isExist(id);
            if(exist){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST); //400
            }else{
                customerBO.updateCustomer(name, address, id);
                out.println("Customer has been updated successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
        }
    }
}
