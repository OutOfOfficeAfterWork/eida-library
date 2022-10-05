package org.outofoffice.lib.core.ui;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.function.Function;

import static java.util.function.Function.identity;

public class EidaSerializationMap {

    private static final Map<Class<?>, Function<String, ?>> FIELD_TYPE_CAST_FUNCTION_MAP = Map.of(
        Long.class, Long::parseLong,
        String.class, identity()
    );

    public static Function<String, ?> get(Class<?> aType, EidaEntity<?> entity) {
        return FIELD_TYPE_CAST_FUNCTION_MAP.getOrDefault(aType, getEidaEntityCastFunction(entity, aType));
    }

    private static <T extends EidaEntity<ID>, ID> Function<String, Object> getEidaEntityCastFunction(T entity, Class<?> type) {
        return s -> {
            try {
                Constructor<? extends EidaEntity> fieldClassConstructor = (Constructor<? extends EidaEntity>) type.getConstructor();
                EidaEntity linkedEntity = fieldClassConstructor.newInstance();
                Class<?> id = (Class<?>) linkedEntity.getClass().getMethod("getIdClass").invoke(entity);
                if (id.equals(Long.class)) {
                    linkedEntity.setId(Long.parseLong(s));
                } else if (id.equals(String.class)) {
                    linkedEntity.setId(s);
                }
                return linkedEntity;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        };
    }

}
