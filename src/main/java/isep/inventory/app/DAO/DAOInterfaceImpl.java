package isep.inventory.app.DAO;

import isep.inventory.app.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class DAOInterfaceImpl <T,Integer> implements DAOInterface<T, Integer> {
    private final Connection connection;

    protected abstract String getTableName();
    protected abstract String getIdColumnName();
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;

    public DAOInterfaceImpl() {
         this.connection = DatabaseConnection.getConnection();
    }



    public T findById(Object id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding entity by ID", e);
        }
    }

    @Override
    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTableName();
        List<T> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(mapResultSetToEntity(rs));
            }
            return results;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all entities", e);
        }
    }

}
