package isep.inventory.app.DAO;

import isep.inventory.app.DatabaseConnection;
import isep.inventory.app.entity.Company;
import isep.inventory.app.entity.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO {
    Connection connection;

    public CompanyDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean createCompany(Company company) {
        String sql = "INSERT into company (name, city, street, postAddress) VALUES (?, ?, ?, ?)";
        try(PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, company.getName());
            pstmt.setString(2, company.getCity());
            pstmt.setString(3, company.getStreet());
            pstmt.setString(4, company.getPostAddress());
            return pstmt.executeUpdate() > 0;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCompany(Company company) {
        String sql ="UPDATE company name=? , city=?, street=?, postAddress=? WHERE id=?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, company.getName());
            pstmt.setString(2, company.getCity());
            pstmt.setString(3, company.getStreet());
            pstmt.setString(4, company.getPostAddress());
            pstmt.setInt(5, company.getId());
            return pstmt.executeUpdate() > 0;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public Company getCompany(int id) {
        String sql = "SELECT * FROM company WHERE id=?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Company(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("city"),
                        rs.getString("street"),
                        rs.getString("postAddress")
                );
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public Company getCompany(Company company) {
        int id = company.getId();
        String sql = "SELECT * FROM company WHERE id=?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Company(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("city"),
                        rs.getString("street"),
                        rs.getString("postAddress")
                );
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<Product> getProducts(Company company) {
        String sql = "SELECT * FROM product WHERE company_id=?";
        int id  = company.getId();
        List<Product> products = new ArrayList<>();
        try(PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery(sql);
            while (rs.next()) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
