package xyz.nulldev.wls.io;

import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nulldev.wls.utils.GSONUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: WebLinkedServer
 * Created: 15/01/16
 * Author: nulldev
 */
public class FileManager {

    static final Type FILE_LIST = new TypeToken<List<ImmutableFile>>(){}.getType();

    File rootDirectory;
    Path rootPath;
    Logger logger = LoggerFactory.getLogger(FileManager.class);

    public FileManager(File rootDirectory) {
        this.rootDirectory = rootDirectory;
        rootPath = rootDirectory.toPath();
    }

    /**
     * Resolve a relative path to the rootDirectory
     * @param path The path to resolve.
     * @return The file
     */
    public File resolveRelative(String path) {
        logger.info("Resolving path: {}", path);
        Path newPath = rootPath.resolve(path);
        return newPath.toFile();
    }

    /**
     * Make sure the user doesn't try to back out of the root directory
     * @param file The file to validate.
     */
    public void validateSecure(File file) {
        if(!file.toPath().startsWith(rootPath)) {
            logger.warn("User attempted to access file out of root directory! ({})", file.getAbsolutePath());
            throw new SecurityException("Attempt to access resource out of root path!");
        }
    }

    public List<ImmutableFile> listFiles(File file) {
        if(!file.exists()) {
            throw new IllegalArgumentException("Path does not exist!");
        }
        File[] list = file.listFiles();
        if(list == null) {
            return new ArrayList<>();
        } else {
            return Arrays.stream(list)
                    .parallel()
                    .map(file1 -> ImmutableFile.fromFile(file1, rootPath))
                    .collect(Collectors.toList());
        }
    }

    public static String fileListToJSON(List<ImmutableFile> fileList) {
        return GSONUtils.getGson().toJson(fileList, FILE_LIST);
    }

    public static List<ImmutableFile> fileListFromJSON(String string) {
        return GSONUtils.getGson().fromJson(string, FILE_LIST);
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    public Path getRootPath() {
        return rootPath;
    }
}
