package de.lukaskoerfer.gradle.magicdraw.util;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides some static utility functions for files
 */
public class FileUtil {
    
    /**
     * Creates a file instance navigating forward from a root
     * @param root
     * @param segments
     * @return
     */
    public static File file(File root, Object... segments) {
        for (Object segment : segments) {
            root = new File(root, segment.toString());
        }
        return root;
    }
    
    /**
     * Puts a list of file paths in a string separated by a path separator (e.g. a semicolon)
     * @param files
     * @return
     */
    public static String stringify(List<File> files) {
        return files.stream()
            .map(File::getAbsolutePath)
            .collect(Collectors.joining(File.pathSeparator));
    }

}
