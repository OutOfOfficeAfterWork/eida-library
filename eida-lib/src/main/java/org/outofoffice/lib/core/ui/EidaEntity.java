package org.outofoffice.lib.core.ui;

import org.outofoffice.common.exception.EidaException;
import org.outofoffice.lib.core.annotation.Id;
import org.outofoffice.lib.util.ClassUtils;

import java.lang.reflect.Field;
import java.util.Arrays;


public interface EidaEntity<ID> {

    default String getTableName() {
        return ClassUtils.toTableName((Class<? extends EidaEntity<ID>>) getClass());
    }

    default ID getId() {
        try {
            Field idField = findIdField();
            idField.setAccessible(true);
            return (ID) idField.get(this);
        } catch (IllegalAccessException e) {
            throw new EidaException(e);
        }
    }

    default void setId(ID value) {
        try {
            Field idField = findIdField();
            idField.setAccessible(true);
            idField.set(this, value);
        } catch (IllegalAccessException e) {
            throw new EidaException(e);
        }

    }

    default boolean shouldAutoGenerateId() {
        Field idField = findIdField();
        return idField.getAnnotation(Id.class).autoGenerated();
    }

    default Class<?> getIdClass() {
        ID id = getId();
        return id.getClass();
    }


    private Field findIdField() {
        Field[] fields = this.getClass().getDeclaredFields();
        return Arrays.stream(fields)
            .filter(f -> f.isAnnotationPresent(Id.class))
            .findFirst()
            .orElseThrow(() -> new EidaException("id not found"));
    }

}