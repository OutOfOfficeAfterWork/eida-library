package org.outofoffice.lib.core.ui;

import lombok.extern.slf4j.Slf4j;
import org.outofoffice.common.exception.EidaException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;

@Slf4j
public class EidaSerializer {

    private static final String NULL_STRING = "<null>";


    public <T extends EidaEntity<ID>, ID> String serialize(T entity) {
        return doSerialize(entity);
    }

    private <T extends EidaEntity<ID>, ID> String doSerialize(T entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        return Arrays.stream(fields)
            .map(field -> getValue(entity, field))
            .map(Object::toString)
            .collect(joining(","));
    }

    private <T extends EidaEntity<ID>, ID> Object getValue(T entity, Field field) {
        try {
            field.setAccessible(true);
            Object value = field.get(entity);
            if (value instanceof EidaEntity<?>) {
                value = ((EidaEntity<?>) value).getId();
            }
            return Optional.ofNullable(value).orElse(NULL_STRING);
        } catch (Exception e) {
            throw new EidaException(e);
        }
    }


    public <T extends EidaEntity<ID>, ID> List<T> deserialize(String schemeString, String tableString, Class<T> entityClass) {
        try {
            return doDeserialize(schemeString, tableString, entityClass);
        } catch (Exception e) {
            throw new EidaException(e);
        }
    }

    private <T extends EidaEntity<ID>, ID> List<T> doDeserialize(String schemeString, String tableString, Class<T> entityClass) throws Exception {
        if (tableString.isEmpty()) return emptyList();

        List<T> entityList = new ArrayList<>();

        String[] lines = tableString.split("\n");
        String[] columns = schemeString.split(",");

        for (String line : lines) {
            String[] values = line.split(",");

            T entity = constructEntity(entityClass, columns, values);
            entityList.add(entity);
        }

        return entityList;
    }

    private <T extends EidaEntity<ID>, ID> T constructEntity(Class<T> entityClass, String[] columns, String[] values) throws Exception {
        if (columns.length != values.length) throw new IllegalStateException("invalid kv mapping");

        Constructor<T> constructor = entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        T entity = constructor.newInstance();

        int n = columns.length;
        for (int i = 0; i < n; i++) {
            if (values[i].equals(NULL_STRING)) continue;

            String column = columns[i];
            String value = values[i];
            Field field = entityClass.getDeclaredField(column);
            field.setAccessible(true);

            Class<?> type = field.getType();

            Function<String, ?> castFunction = EidaSerializationMap.get(type, entity);
            Object object = castFunction.apply(value);
            field.set(entity, object);
        }
        return entity;
    }

}
