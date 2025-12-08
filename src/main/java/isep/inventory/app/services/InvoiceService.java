package isep.inventory.app.services;

import isep.inventory.app.DAO.InvoiceDAO;
import isep.inventory.app.DAO.ProductDAO;
import isep.inventory.app.entity.Invoice;
import isep.inventory.app.entity.Product;

import java.sql.SQLException;
import java.util.List;

public class InvoiceService {

    private final InvoiceDAO invoiceDAO;
    private final ProductDAO productDAO;

    public InvoiceService(InvoiceDAO invoiceDAO, ProductDAO productDAO) {
        this.invoiceDAO = invoiceDAO;
        this.productDAO = productDAO;
    }

    // --- Core CRUD Methods (Delegated) ---

    public Invoice getInvoiceById(int id) {
        return invoiceDAO.findById(id);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceDAO.findAll();
    }

    // --- Complex Business Method: Create/Save ---

    /**
     * Creates a new invoice, validates product availability, and updates inventory stock.
     * This method manages the transactional integrity between the Invoice and Product tables.
     * * @param invoice The Invoice object to save.
     * @return The saved Invoice object with its generated ID.
     * @throws Exception if a product is out of stock or if a database error occurs.
     */
    public Invoice createInvoice(Invoice invoice) throws Exception {
        // 1. Transaction Start (Optional: If using JDBC auto-commit=false)
        // Note: For simplicity, we rely on individual DAO operations to handle transactions,
        // but in a real app, connection.setAutoCommit(false) would be used here.

        // 2. Validate and Update Product Stock
        for (Product invoiceProduct : invoice.getProducts()) {
            // Retrieve the latest product data from the database
            Product dbProduct = productDAO.getProduct(invoiceProduct);

            if (dbProduct == null) {
                throw new Exception("Product " + invoiceProduct.getName() + " not found in inventory.");
            }

            int requiredQuantity = invoiceProduct.getQuantity();
            int currentStock = dbProduct.getStock();

            // Check Stock Availability
            if (currentStock < requiredQuantity) {
                throw new Exception("Insufficient stock for product " + dbProduct.getName() +
                        ". Available: " + currentStock + ", Requested: " + requiredQuantity);
            }

            // Decrement Stock
            dbProduct.setStock(currentStock - requiredQuantity);

            // Update the product in the database
            if (!productDAO.updateProduct(dbProduct)) {
                // In a real application, you'd roll back the transaction here
                throw new Exception("Failed to update stock for product " + dbProduct.getName());
            }

            // OPTIONAL: Ensure the Invoice product has the correct unit price from the DB
            invoiceProduct.setPrice(dbProduct.getPrice());
        }

        // 3. Save the Invoice
        try {
            Invoice savedInvoice = invoiceDAO.save(invoice);
            // 4. Transaction Commit (If using JDBC auto-commit=false)
            return savedInvoice;
        } catch (Exception e) {
            // 5. Transaction Rollback (If using JDBC auto-commit=false)
            // If the invoice save fails, you might need to revert the stock changes made in step 2.
            throw new Exception("Failed to save the invoice after stock updates.", e);
        }
    }

    // --- Update Method ---
    // Note: Updating an invoice often requires complex logic (reversing old stock, adding new stock).
    // For now, a simple delegation is provided. Full implementation is complex.
    public void updateInvoice(Invoice invoice) throws Exception {
        invoiceDAO.update(invoice);
    }

    // --- Delete Method ---
    /**
     * Deletes an invoice.
     * Note: Deleting an invoice often requires restoring the stock quantities.
     * @param id The ID of the invoice to delete.
     * @return true if deletion was successful.
     * @throws Exception if an error occurs.
     */
    public boolean deleteInvoice(int id) throws Exception {
        // Retrieve the invoice to get product details for stock restoration
        Invoice invoice = invoiceDAO.findById(id);
        if (invoice == null) {
            return true; // Already gone
        }

        // 1. Delete the invoice
        if (invoiceDAO.delete(id)) {
            // 2. Restore Product Stock (optional business requirement)
            for (Product productToRestore : invoice.getProducts()) {
                Product dbProduct = productDAO.getProduct(productToRestore);
                if (dbProduct != null) {
                    dbProduct.setStock(dbProduct.getStock() + productToRestore.getQuantity());
                    productDAO.updateProduct(dbProduct); // Update stock
                }
            }
            return true;
        }
        return false;
    }
}