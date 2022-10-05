package org.outofoffice.lib.util;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;


public class LogUtils {

    public static String indentedFormat(String... lines) {
        return Arrays.stream(lines).map(line -> "\n\t" + line).collect(joining());
    }

}
