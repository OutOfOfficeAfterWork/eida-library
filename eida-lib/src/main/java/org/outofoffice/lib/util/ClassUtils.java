package org.outofoffice.lib.util;

import org.outofoffice.lib.core.ui.EidaEntity;
import org.outofoffice.lib.core.ui.EidaRepository;

import java.lang.reflect.ParameterizedType;


public class ClassUtils {

    public static Class<?> classForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }


    public static <T extends EidaEntity<ID>, ID> String toTableName(Class<T> entityClass) {
        return entityClass.getSimpleName();
    }

    public static <T extends EidaEntity<ID>, ID> Class<T> toEntityClass(Class<? extends EidaRepository<T, ID>> repositoryClass) {
        ParameterizedType generic = (ParameterizedType) repositoryClass.getGenericSuperclass();
        return (Class<T>) generic.getActualTypeArguments()[0];
    }

}
