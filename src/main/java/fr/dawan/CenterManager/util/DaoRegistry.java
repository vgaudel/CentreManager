package fr.dawan.CenterManager.util;

import java.util.HashMap;
import java.util.Map;

import fr.dawan.CenterManager.dao.GenericDao;

public class DaoRegistry {
    private static final Map<Class<?>, GenericDao<?>> registry = new HashMap<>();

    private DaoRegistry() {}

    public static <T> void registerDao(Class<T> clazz, GenericDao<?> dao) {
        registry.put(clazz, dao);
    }

    @SuppressWarnings("unchecked")
    public static <T> GenericDao<?> getDao(Class<T> clazz) {
        return (GenericDao<?>) registry.get(clazz);
    }
}