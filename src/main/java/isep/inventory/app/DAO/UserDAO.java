package isep.inventory.app.DAO;

import isep.inventory.app.DatabaseConnection;
import isep.inventory.app.entity.Role;
import isep.inventory.app.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public boolean createUser(User user) {
        String sql = "INSERT INTO public.user (username, password,firstName,lastName) VALUES (?,?,?,?,?)";
        try(PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setString(5, user.getRole().name());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
                return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE public.user SET firstName = ?, lastName = ?, username = ?, role = ? WHERE id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getUsername());
            pstmt.setInt(5, user.getId());
            pstmt.setString(4, user.getRole().name());

            return pstmt.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return  false;
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM public.user WHERE id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM public.user";

        try(Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        Role.valueOf(rs.getString("role")),
                        rs.getInt("company_id")
                ));
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return users;
    }

    public User getUser(int id) {
        String sql = "SELECT * FROM public.user WHERE id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        Role.valueOf(rs.getString("role")),
                        rs.getInt("company_id"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM public.user WHERE username = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role")),
                        rs.getInt("company_id"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

}
