package org.outofoffice.lib.context;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outofoffice.lib.exception.EidaException;
import org.outofoffice.lib.core.socket.EidaSocketClient;
import org.outofoffice.lib.core.client.EidaManagerClient;
import org.outofoffice.lib.core.client.EidaShardClient;
import org.outofoffice.lib.core.query.EidaDllGenerator;
import org.outofoffice.lib.core.query.EidaDmlGenerator;
import org.outofoffice.lib.core.ui.EidaEntity;
import org.outofoffice.lib.core.ui.EidaRepository;
import org.outofoffice.lib.core.ui.EidaSerializer;
import org.outofoffice.lib.util.ClassUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class EidaContext {

    private static EidaPropertyLoader propertyLoader;
    private static EidaRepositoryScanner repositoryScanner;

    private static final Map<Class<? extends EidaEntity<?>>, EidaRepository<? extends EidaEntity<?>, ?>> MAP = new HashMap<>();

    public static EidaRepository<? extends EidaEntity<?>, ?> getRepository(Class<? extends EidaEntity<?>> entityClass) {
        return MAP.get(entityClass);
    }

    public static Collection<EidaRepository<? extends EidaEntity<?>, ?>> getRepositories(){
        return MAP.values();
    }


    public static void init(Class<?> mainClass, EidaSocketClient socket) {
        log.info("Initialize Starting: socket - {}", socket);
        propertyLoader = new EidaPropertyLoader(mainClass);
        repositoryScanner = new EidaRepositoryScanner(mainClass);
        try {
            doInit(socket);
        } catch (Exception e) {
            throw new EidaException(e);
        }
        log.info("Initialize Finished");
    }

    private static <T extends EidaEntity<ID>, ID> void doInit(EidaSocketClient socket) throws Exception {
        String managerServerUrl = propertyLoader.getManagerServerUrl();

        EidaManagerClient managerClient = new EidaManagerClient(new EidaDllGenerator(), socket, managerServerUrl);
        EidaShardClient shardClient = new EidaShardClient(new EidaDmlGenerator(), socket);
        EidaSerializer serializer = new EidaSerializer();

        List<? extends Class<?>> repositoryClasses = repositoryScanner.scan();

        for (Class<?> repositoryClass : repositoryClasses) {
            Class<EidaRepository<T, ID>> castedRepositoryClass = (Class<EidaRepository<T, ID>>) repositoryClass;
            Class<? extends EidaEntity<ID>> entityClass = ClassUtils.toEntityClass(castedRepositoryClass);
            MAP.put(entityClass, repositoryInstance(castedRepositoryClass, managerClient, shardClient, serializer));
        }
    }

    private static <T extends EidaEntity<ID>, ID> EidaRepository<T, ID> repositoryInstance(
            Class<EidaRepository<T, ID>> repositoryClass, EidaManagerClient managerClient, EidaShardClient shardClient, EidaSerializer serializer
    ) throws Exception {
        EidaRepository<T, ID> repository = repositoryClass.getDeclaredConstructor().newInstance();
        repository.init(managerClient, shardClient, serializer);
        return repository;
    }

}
