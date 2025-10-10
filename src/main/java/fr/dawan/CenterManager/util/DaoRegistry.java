package fr.dawan.CenterManager.util;

import java.util.HashMap;
import java.util.Map;

import fr.dawan.CenterManager.dao.GenericDao;

public class DaoRegistry {
    private static final Map<Class<?>, GenericDao<?>> daoRegistry = new HashMap<>();

    public static <T> void registerDao(Class<T> clazz, GenericDao<?> dao) {
        daoRegistry.put(clazz, dao);
    }

    @SuppressWarnings("unchecked")
    public static <T> GenericDao<?> getDao(Class<T> clazz) {
        return (GenericDao<?>) daoRegistry.get(clazz);
    }
}
