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

    public boolean createProduct(Product product){
        String sql = "INSERT INTO products (name, description, stock, available, price, category, source) VALUES (?,?,?,?,?)";
        try(PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setInt(3, product.getStock());
            pstmt.setBoolean(4, product.isAvailable());
            pstmt.setDouble(5, product.getPrice());
            pstmt.setString(6, product.getCategory());
            pstmt.setString(7, product.getSourceCompany());
            int rowsAffected = pstmt.executeUpdate();

            if(rowsAffected>0){
                ResultSet rs = pstmt.getGeneratedKeys();
                if(rs.next()){
                    product.setId(rs.getInt(1));
                }
                return true;
            }

        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean updateProduct(Product product){
        String sql = "UPDATE product name=?, description=?, stock=?, available=?, price=?, category=?, source=? WHERE id=?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
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

    public  boolean deleteProduct(Product product){
        String sql = "DELETE FROM product WHERE id=?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, product.getId());
            return pstmt.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public Product getProduct(Product product){
        String sql = "SELECT * FROM product WHERE id=?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            pstmt.setInt(1, product.getId());
            pstmt.executeQuery();
            ResultSet rs = pstmt.getGeneratedKeys();
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
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<Product> getAllProducts(){
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product";
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
