package com.ita.util;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by itachi on 14-02-2015.
 */
public class FileUtils {
    public static List<Path> convertListFiletoListPath(List<File> listOfFile) {
        return listOfFile.stream().map(File::toPath).collect(Collectors.toList());
    }
}
