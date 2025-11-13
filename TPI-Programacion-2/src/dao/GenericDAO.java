package dao;

import java.util.List;

public interface GenericDAO<T> {
    
    void save(T entity) throws Exception;
    
    
    T findById(int id) throws Exception;
    List<T> findAll() throws Exception;
    void update(T entity) throws Exception;
    void delete(int id) throws Exception;
    
    void saveTx(T entity, java.sql.Connection conn) throws Exception;
}