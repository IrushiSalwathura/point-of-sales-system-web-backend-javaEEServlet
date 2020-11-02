package lk.ijse.dep.business.custom;

import lk.ijse.dep.business.SuperBO;
import lk.ijse.dep.dto.CustomerDTO;

import java.util.List;

public interface CustomerBO extends SuperBO {
    String getNewCustomerId() throws Exception;
    List<CustomerDTO> getAllCustomers() throws Exception;
    CustomerDTO getCustomer(String id) throws Exception;
    void saveCustomer(String id, String name, String address) throws Exception;
    void updateCustomer(String name, String address, String id) throws Exception;
    void deleteCustomer(String customerId) throws Exception;
    boolean isExist(String id) throws Exception;

}
