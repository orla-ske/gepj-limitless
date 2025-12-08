package isep.inventory.app.DAO;

import isep.inventory.app.DatabaseConnection;
import isep.inventory.app.entity.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    Connection connection;

    public ProductDAO(){
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Adds a new product to the database.
     * @param product The product object to save.
     * @return true if the product was successfully created and its ID set.
     */
    public boolean createProduct(Product product){
        // Corrected SQL: The Product object seems to have 7 fields besides ID, but the SQL had 7 placeholders.
        // Assuming your table has 7 columns: name, description, stock, available, price, category, source.
        String sql = "INSERT INTO products (name, description, stock, available, price, category, source) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setInt(3, product.getStock());
            pstmt.setBoolean(4, product.isAvailable());
            pstmt.setDouble(5, product.getPrice());
            pstmt.setString(6, product.getCategory());
            pstmt.setString(7, product.getSourceCompany()); // Source/Company name

            int rowsAffected = pstmt.executeUpdate();

            if(rowsAffected > 0){
                try(ResultSet rs = pstmt.getGeneratedKeys()){
                    if(rs.next()){
                        product.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates an existing product in the database.
     * @param product The product object with updated values.
     * @return true if the product was successfully updated.
     */
    public boolean updateProduct(Product product){
        // Corrected SQL: Proper SET syntax is required for UPDATE statements.
        String sql = "UPDATE products SET name=?, description=?, stock=?, available=?, price=?, category=?, source=? WHERE id=?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setInt(3, product.getStock());
            pstmt.setBoolean(4, product.isAvailable());
            pstmt.setDouble(5, product.getPrice());
            pstmt.setString(6, product.getCategory());
            pstmt.setString(7, product.getSourceCompany());

            pstmt.setInt(8, product.getId());
            return pstmt.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes a product from the database.
     * @param product The product object to delete.
     * @return true if the product was successfully deleted.
     */
    public boolean deleteProduct(Product product){
        // Corrected SQL: Table name was "product", should be consistent, e.g., "products".
        String sql = "DELETE FROM products WHERE id=?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, product.getId());
            return pstmt.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a single product by its ID.
     * @param product The product object containing the ID to search for.
     * @return The found Product object or null.
     */
    public Product getProduct(Product product){
        // Corrected logic: Use executeQuery and check the resulting ResultSet,
        // DO NOT use getGeneratedKeys for a SELECT statement.
        String sql = "SELECT * FROM products WHERE id=?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, product.getId());
            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    return new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("stock"),
                            rs.getBoolean("available"),
                            rs.getDouble("price"),
                            rs.getString("category"),
                            rs.getString("source")
                    );
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all products from the database.
     * @return A list of all Product objects.
     */
    public List<Product> getAllProducts(){
        List<Product> products = new ArrayList<>();
        // Corrected SQL: Table name was "product", assuming "products" is correct.
        String sql = "SELECT * FROM products";
        try(Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("stock"),
                        rs.getBoolean("available"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        rs.getString("source")
                ));
            }
            return products;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}