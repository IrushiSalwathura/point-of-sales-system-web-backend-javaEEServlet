package lk.ijse.dep.business.custom;

import lk.ijse.dep.business.SuperBO;
import lk.ijse.dep.dto.ItemDTO;

import java.util.List;

public interface ItemBO extends SuperBO {
    String getNewItemCode() throws Exception;
    List<ItemDTO> getAllItems() throws Exception;
    ItemDTO getItem(String code) throws Exception;
    void saveItem(String code, String description, double unitPrice, int qtyOnHand) throws Exception;
    void updateItem(String description, double unitPrice, int qtyOnHand, String code) throws Exception;
    void deleteItem(String itemCode) throws Exception;
    boolean isExist(String code) throws Exception;
}
