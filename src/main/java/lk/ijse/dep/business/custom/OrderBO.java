package lk.ijse.dep.business.custom;

import lk.ijse.dep.business.SuperBO;
import lk.ijse.dep.dto.OrderDTO;
import lk.ijse.dep.dto.OrderDetailDTO;


import java.util.List;

public interface OrderBO extends SuperBO {
    void placeOrder(OrderDTO order, List<OrderDetailDTO> orderDetails) throws Exception;
    String getNewOrderId() throws Exception;
    OrderDTO getOrder(String id) throws Exception;
    //List<OrderDTO> searchOrder() throws Exception;
    boolean isExist(String id) throws Exception;
}
