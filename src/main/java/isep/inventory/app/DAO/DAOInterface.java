package isep.inventory.app.DAO;

import java.util.List;

public interface DAOInterface<T, ID> {
    T findById(ID id);
    List<T> findAll();
}
