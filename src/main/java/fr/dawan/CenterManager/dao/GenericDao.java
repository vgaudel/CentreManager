package fr.dawan.CenterManager.dao;

import java.sql.SQLException;
import java.util.List;

import fr.dawan.CenterManager.model.Displayable;

public interface GenericDao<T extends Displayable> {
    List<T> getAll() throws SQLException;
    T insert(T entity) throws SQLException;
    void update(T entity) throws SQLException;
    void delete(T entity) throws SQLException;
    T createFromName(String name) throws SQLException;
}
