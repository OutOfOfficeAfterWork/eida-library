package org.outofoffice.lib.context;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static java.io.File.separator;

@Slf4j
@RequiredArgsConstructor
public class EidaPropertyLoader {

    private static final String fileName = "eida.properties";

    private final Class<?> mainClass;

    public String getManagerServerUrl() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(getPropertyFileFromMainClass()));

        String managerServerUrl = properties.getProperty("managerServerUrl");
        log.debug("Property load Finished: managerServerUrl - {}", managerServerUrl);
        return managerServerUrl;
    }

    private File getPropertyFileFromMainClass() {
        String[] mainClassPath = mainClass.getResource(".").getPath().split(separator);
        StringJoiner buildRootPath = new StringJoiner(separator);
        for (String hierarchy : mainClassPath) {
            if (hierarchy.equals("classes")) break;
            buildRootPath.add(hierarchy);
        }
        String path = String.join(separator, buildRootPath.toString(), "resources", "main", fileName);
        return new File(path);
    }
}
