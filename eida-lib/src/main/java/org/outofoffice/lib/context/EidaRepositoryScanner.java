package org.outofoffice.lib.context;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outofoffice.lib.core.ui.EidaRepository;
import org.outofoffice.lib.util.ClassUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.io.File.separator;
import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
public class EidaRepositoryScanner {

    private static final String ABSTRACT_REPOSITORY_NAME = EidaRepository.class.getName();
    private static final String GRADLE_MAIN_PATH = "/build/classes/java/main/";
    private static final String GRADLE_TEST_PATH = "/build/classes/java/test/";
    private static final String EXTENSION = ".class";

    private static final Class<EidaRepository> EIDA_REPOSITORY_CLASS = EidaRepository.class;

    private final Class<?> mainClass;


    public List<? extends Class<?>> scan() throws IOException {
        String mainClassPath = mainClass.getResource(".").getPath();
        String javaMainPath = mainClassPath.replace(GRADLE_TEST_PATH, GRADLE_MAIN_PATH).split(GRADLE_MAIN_PATH)[0] + GRADLE_MAIN_PATH;
        try (Stream<Path> paths = Files.walk(Paths.get(javaMainPath))) {
            List<String> repositoryClassNames = paths
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .filter(path -> path.endsWith("Repository.class"))
                .map(this::diskPathToFullClassName)
                .filter(fullClassName -> !fullClassName.equals(ABSTRACT_REPOSITORY_NAME))
                .collect(toList());

            List<? extends Class<?>> repositoryClasses = repositoryClassNames.stream()
                .map(ClassUtils::classForName)
                .filter(clazz -> clazz.getSuperclass().equals(EIDA_REPOSITORY_CLASS))
                .collect(Collectors.toList());

            log.info("Repository scan Finished: names- {}", repositoryClasses.stream().map(Class::getSimpleName).collect(Collectors.joining(",")));
            return repositoryClasses;
        }
    }

    private String diskPathToFullClassName(String diskPath) {
        String relativePath = diskPath.split(GRADLE_MAIN_PATH)[1];
        String extensionExcluded = relativePath.replace(EXTENSION, "");
        return extensionExcluded.replace(separator, ".");
    }
}
