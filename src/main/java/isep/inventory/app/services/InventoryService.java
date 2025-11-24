package isep.inventory.app.services;

import isep.inventory.app.entity.Product;
import isep.inventory.app.DAO.ProductDAO;

public class InventoryService {
    private final ProductDAO productDAO;

    public InventoryService(){
        productDAO = new ProductDAO();
    }
    public boolean addStock(Product product) {
        product.setStock(product.getStock() + 1);
        return productDAO.updateProduct(product);
    }

    public boolean removeStock(Product product) {
        product.setStock(product.getStock() - 1);
        return productDAO.updateProduct(product);
    }

    public int remainingStock(Product product) {
        return product.getStock();
    }

    public boolean isProductAvailable(Product product) {
        return product.isAvailable();
    }
}
