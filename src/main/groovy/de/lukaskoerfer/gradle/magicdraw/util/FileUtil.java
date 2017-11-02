package de.lukaskoerfer.gradle.magicdraw.util;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtil {
    
    public static File file(File file, Object... segments) {
        for (Object segment : segments) {
            file = new File(file, segment.toString());
        }
        return file;
    }
    
    public static String stringify(List<File> files) {
        return String.join(File.pathSeparator, files.stream()
            .map(File::getAbsolutePath)
            .collect(Collectors.toList()));
    }

}
