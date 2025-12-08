package isep.inventory.app.DAO;

import isep.inventory.app.DatabaseConnection; // Needed for connection in constructor
import isep.inventory.app.entity.Company;
import isep.inventory.app.entity.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO {
    Connection connection;

    // FIX: Changed constructor to use DatabaseConnection.getConnection()
    public CompanyDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Adds a new company to the database.
     * @param company The company object to save.
     * @return true if the company was successfully created.
     */
    public boolean createCompany(Company company) {
        String sql = "INSERT INTO company (name, city, street, postAddress) VALUES (?, ?, ?, ?)";
        try(PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, company.getName());
            pstmt.setString(2, company.getCity());
            pstmt.setString(3, company.getStreet());
            pstmt.setString(4, company.getPostAddress());

            int rowsAffected = pstmt.executeUpdate();

            if(rowsAffected > 0){
                try(ResultSet rs = pstmt.getGeneratedKeys()){
                    if(rs.next()){
                        // Set the auto-generated ID back on the Company object
                        company.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates an existing company in the database.
     * @param company The company object with updated values.
     * @return true if the company was successfully updated.
     */
    public boolean updateCompany(Company company) {
        // Corrected SQL: Proper SET syntax is required for UPDATE statements.
        String sql ="UPDATE company SET name=?, city=?, street=?, postAddress=? WHERE id=?";

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

    /**
     * Retrieves a single company by its ID.
     * @param id The ID of the company to search for.
     * @return The found Company object or null.
     */
    public Company getCompany(int id) {
        String sql = "SELECT * FROM company WHERE id=?";

        // FIX: Removed Statement.RETURN_GENERATED_KEYS, not needed for SELECT
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, id);
            try(ResultSet rs = pstmt.executeQuery()){
                if (rs.next()) {
                    return new Company(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("city"),
                            rs.getString("street"),
                            rs.getString("postAddress")
                    );
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a single company based on the ID of the provided Company object.
     * @param company The company object containing the ID to search for.
     * @return The found Company object or null.
     */
    public Company getCompany(Company company) {
        // This method can simply call the other getCompany(int id)
        return getCompany(company.getId());
    }

    /**
     * Retrieves all products associated with a specific company.
     * @param company The company whose products are to be retrieved.
     * @return A list of Product objects.
     */
    public List<Product> getProducts(Company company) {
        // Assuming the 'products' table has a 'company_id' column for a foreign key relationship
        String sql = "SELECT * FROM products WHERE source=?"; // Assuming 'source' column in products holds the company name

        // OR if you use FK ID: String sql = "SELECT * FROM products WHERE company_id=?";

        String companyIdentifier = company.getName(); // Using company name as identifier, based on ProductDAO's 'source' field

        List<Product> products = new ArrayList<>();
        // FIX: Execute the PreparedStatement, not the raw SQL string
        try(PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, companyIdentifier); // Bind the identifier (name or ID)
            try(ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    products.add(new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("stock"),
                            rs.getBoolean("available"),
                            rs.getDouble("price"),
                            rs.getString("category"),
                            rs.getString("source") // source is the company name
                    ));
                }
            }
            return products;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}