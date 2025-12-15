package isep.inventory.app.services;

import isep.inventory.app.DAO.UserDAO;
import isep.inventory.app.entity.Role;
import isep.inventory.app.entity.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AuthenticationService {
    private final UserDAO userDAO;
    public AuthenticationService() {
        userDAO = new UserDAO();
    }

    public boolean login(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user == null) return false;
        String hashedPassword = hashPassword(password);
        String pol = user.getPassword();
        return pol.equals(hashedPassword);
    }

    public boolean register(String username, String password, String firstName, String lastName, String role, int companyId) {
        if (userDAO.getUserByUsername(username) != null) return false;
        Role userRole = Role.valueOf(role);
        User user = new User(username, hashPassword(password), firstName, lastName, userRole, companyId);
        return userDAO.createUser(user);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());

            // Convert to hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public User getUserByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }
}
