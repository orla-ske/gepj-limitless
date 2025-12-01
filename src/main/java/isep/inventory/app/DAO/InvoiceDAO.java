package isep.inventory.app.DAO;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import isep.inventory.app.DatabaseConnection;
import isep.inventory.app.entity.Invoice;
import isep.inventory.app.entity.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO{
    private final Connection connection;
    private final ObjectMapper mapper;

    public InvoiceDAO(){
        this.connection= DatabaseConnection.getConnection();
        this.mapper = new ObjectMapper();
    }

    public Invoice findById(Object id) {
        String sql = "SELECT * FROM invoices WHERE id=?";
        Invoice invoice = null;

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setObject(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                invoice = new Invoice();
                invoice.setId(rs.getInt("id"));
                invoice.setDate(rs.getDate("date"));
                invoice.setDestinationCompany(rs.getString("destinationCompany"));
                invoice.setSourceCompany(rs.getString("sourceCompany"));
                String productsJson = rs.getString("productsJson");

                if(productsJson != null && !productsJson.isEmpty() ){
                    // Use the initialized mapper
                    List<Product> products = mapper.readValue(
                            productsJson,
                            new TypeReference<List<Product>>() {}
                    );
                    invoice.setProducts(products);
                } else {
                    invoice.setProducts(new ArrayList<>());
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return invoice;
    }

    public List<Invoice> findAll(){
        String sql = "SELECT * FROM invoices";
        List<Invoice> invoices = new ArrayList<>();

        try(Statement stmt = connection.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()){
                Invoice invoice = new Invoice();
                invoice.setId(rs.getInt("id"));
                invoice.setDate(rs.getDate("date"));
                invoice.setDestinationCompany(rs.getString("destinationCompany"));
                invoice.setSourceCompany(rs.getString("sourceCompany"));
                String productsJson = rs.getString("productsJson");

                if(productsJson != null && !productsJson.isEmpty() ){
                    List<Product> products = mapper.readValue(
                            productsJson,
                            new TypeReference<List<Product>>() {}
                    );
                    invoice.setProducts(products);
                } else {
                    invoice.setProducts(new ArrayList<>());
                }
                invoices.add(invoice);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return invoices;
    }

    public Invoice save(Invoice invoice) throws Exception {
        String sql = "INSERT INTO invoices (date, destinationCompany, sourceCompany, productsJson) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // 1. Serialize the list of products into JSON
            String productsJson = mapper.writeValueAsString(invoice.getProducts());

            // 2. Set parameters
            pstmt.setDate(1, (Date) invoice.getDate());
            pstmt.setString(2, invoice.getDestinationCompany());
            pstmt.setString(3, invoice.getSourceCompany());
            pstmt.setString(4, productsJson); // Store JSON string

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating invoice failed, no rows affected.");
            }

            // 3. Retrieve the auto-generated ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    invoice.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating invoice failed, no ID obtained.");
                }
            }
            return invoice;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error while saving invoice.", e);
        }
    }

    public void update(Invoice invoice) throws Exception {
        String sql = "UPDATE invoices SET date=?, destinationCompany=?, sourceCompany=?, productsJson=? WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String productsJson = mapper.writeValueAsString(invoice.getProducts());

            // 2. Set parameters
            pstmt.setDate(1, (Date) invoice.getDate());
            pstmt.setString(2, invoice.getDestinationCompany());
            pstmt.setString(3, invoice.getSourceCompany());
            pstmt.setString(4, productsJson); // Store JSON string
            pstmt.setInt(5, invoice.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error while updating invoice.", e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM invoices WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}