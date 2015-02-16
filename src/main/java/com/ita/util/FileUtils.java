package com.ita.util;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {
    public static List<Path> convertListFiletoListPath(List<File> listOfFile) {
        return listOfFile.stream().map(File::toPath).collect(Collectors.toList());
    }
}
