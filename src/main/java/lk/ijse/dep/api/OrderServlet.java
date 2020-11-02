package lk.ijse.dep.api;

import lk.ijse.dep.business.custom.ItemBO;
import lk.ijse.dep.business.custom.OrderBO;
import lk.ijse.dep.dto.ItemDTO;
import lk.ijse.dep.dto.OrderDTO;
import lk.ijse.dep.entity.OrderDetail;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@WebServlet(name = "OrderServlet", urlPatterns = "/orders")
public class OrderServlet extends HttpServlet {
    private OrderBO orderBO;

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
        orderBO = ((AnnotationConfigApplicationContext)(getServletContext().getAttribute("ctx"))).getBean(OrderBO.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        try (PrintWriter out = resp.getWriter()){
            if(id == null){
                String newOrderId = orderBO.getNewOrderId();
                resp.setContentType("application/json");
                Jsonb jsonb = JsonbBuilder.create();
                String json = jsonb.toJson(newOrderId);
                out.println(json);
            }else{
                try {
                    OrderDTO order = orderBO.getOrder(id);
                    out.println(order);
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
        String id = req.getParameter("id");
        String date = req.getParameter("date");
        String customerId = req.getParameter("customerId");
        String orderDetails = req.getParameter("orderDetails");

        if(id == null || date == null || customerId == null || orderDetails == null || !id.matches("OD\\d{3}") || !customerId.matches("C\\d{3}")){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST); //400
            return;
        }
        resp.setContentType("text/plain");

        try (PrintWriter out = resp.getWriter()){
            boolean exist = orderBO.isExist(id);
            if(exist){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);//400
            }else{
                //orderBO.placeOrder(order,orderDetails);
                resp.setStatus(HttpServletResponse.SC_CREATED); //201
                out.println("Item has been saved successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
        }
    }
}
