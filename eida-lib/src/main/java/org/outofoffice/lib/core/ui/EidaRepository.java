package org.outofoffice.lib.core.ui;

import org.outofoffice.common.exception.EidaException;
import org.outofoffice.lib.context.EidaContext;
import org.outofoffice.lib.core.client.EidaManagerClient;
import org.outofoffice.lib.core.client.EidaShardClient;
import org.outofoffice.lib.util.ClassUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;


public abstract class EidaRepository<T extends EidaEntity<ID>, ID> {

    private EidaManagerClient managerClient;
    private EidaShardClient shardClient;
    private EidaSerializer serializer;


    public void init(EidaManagerClient managerClient, EidaShardClient shardClient, EidaSerializer serializer) {
        this.managerClient = managerClient;
        this.shardClient = shardClient;
        this.serializer = serializer;
    }


    private Class<? extends EidaRepository<T, ID>> repositoryClass() {
        return (Class<? extends EidaRepository<T, ID>>) getClass();
    }

    private Class<T> entityClass() {
        return ClassUtils.toEntityClass(repositoryClass());
    }

    private String tableName() {
        return ClassUtils.toTableName(entityClass());
    }


    public void insert(T entity) {
        ID id = entity.getId();
        if (id == null && entity.shouldAutoGenerateId()) {
            Long l = managerClient.nextVal();
            id = (ID) l;
            entity.setId(id);
        }

        if (id == null) throw new EidaException("key should not be null");
        if (isExist(id)) throw new EidaException("key is duplicated");

        String serialized = serializer.serialize(entity);
        String destinationShardUrl = managerClient.getDestination(tableName());
        shardClient.insert(destinationShardUrl, tableName(), serialized);
        managerClient.postShardUrl(destinationShardUrl, tableName(), id);
    }

    public void update(T entity) {
        String serialized = serializer.serialize(entity);
        String source = managerClient.getSource(tableName(), entity.getId());
        String sourceShardUrl = source.split("\n", 2)[0];
        shardClient.update(sourceShardUrl, tableName(), serialized);
    }

    public void delete(ID id) {
        String source = managerClient.getSource(tableName(), id);
        String sourceShardUrl = source.split("\n", 2)[0];
        shardClient.delete(sourceShardUrl, tableName(), id);
        managerClient.deleteShardUrl(sourceShardUrl, tableName(), id);
    }

    public void deleteAll() {
        listAll().forEach(entity -> delete(entity.getId()));
    }

    public Optional<T> find(ID id) {
        String[] response = managerClient.getSource(tableName(), id).split("\n", 2);

        String sourceShardUrl = response[0];
        if (sourceShardUrl.isEmpty()) return Optional.empty();

        String schemeString = response[1];
        String tableString = shardClient.selectById(sourceShardUrl, tableName(), id);

        List<T> entities = serializer.deserialize(schemeString, tableString, entityClass());
        if (entities.size() > 1) throw new EidaException("return value size > 1");
        return entities.stream().findFirst();
    }

    public List<T> listAll() {
        String[] response = managerClient.getSources(tableName()).split("\n");
        String shardUrlsString = response[0];
        String schemeString = response[1];

        List<String> shardUrls = stream(shardUrlsString.split(","))
            .filter(s -> !s.isEmpty())
            .collect(toList());

        StringBuilder packedTableBuilder = new StringBuilder();
        for (String shardUrl : shardUrls) {
            String bodyString = shardClient.selectAll(shardUrl, tableName());
            packedTableBuilder.append(bodyString).append("\n");
        }
        String packedTableString = packedTableBuilder.toString();
        return serializer.deserialize(schemeString, packedTableString, entityClass());
    }

    public Optional<T> joinFind(ID id, String fieldName) {
        return find(id).map(entity -> join(entity, fieldName));
    }

    private <J extends EidaEntity<FK>, FK> T join(T entity, String fieldName) {
        Class<? extends EidaEntity> entityClass = entity.getClass();

        try {
            Field field = entityClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            J emptyTarget = (J) field.get(entity);
            if (emptyTarget == null) return entity;

            FK targetId = emptyTarget.getId();

            Class<J> joinTargetClass = (Class<J>) entityClass.getDeclaredField(fieldName).getType();
            EidaRepository<J, FK> joinTargetRepository = (EidaRepository<J, FK>) EidaContext.getRepository(joinTargetClass);

            J foundTarget = joinTargetRepository.find(targetId).orElse(null);
            field.set(entity, foundTarget);

            return entity;
        } catch (Exception e) {
            throw new EidaException(e);
        }
    }

    public List<T> list(Predicate<T> where) {
        return listAll().stream().filter(where).collect(toList());
    }

    public List<T> joinList(Predicate<T> where, String fieldName) {
        return list(where).stream()
            .map(entity -> join(entity, fieldName))
            .collect(toList());
    }

    public boolean isExist(ID id) {
        String[] response = managerClient.getSource(tableName(), id).split("\n", 2);
        String sourceShardUrl = response[0];
        return !sourceShardUrl.isEmpty();
    }

}
