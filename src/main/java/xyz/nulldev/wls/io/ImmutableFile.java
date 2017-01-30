package xyz.nulldev.wls.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

/**
 * Project: WebLinkedServer
 * Created: 15/01/16
 * Author: nulldev
 */
public class ImmutableFile {
    private final String name;
    private final boolean isDirectory;
    private final String relativePath;
    private final long size;
    private final FileTime lastModificationTime;

    private static Logger logger = LoggerFactory.getLogger(ImmutableFile.class);

    public ImmutableFile(FileTime lastModificationTime, long size, String relativePath, boolean isDirectory, String name) {
        this.lastModificationTime = lastModificationTime;
        this.size = size;
        this.relativePath = relativePath;
        this.isDirectory = isDirectory;
        this.name = name;
    }

    public static ImmutableFile fromFile(File file, Path rootDirectory) {
        try {
            return new ImmutableFile(Files.getLastModifiedTime(file.toPath()),
                    file.length(),
                    rootDirectory.relativize(file.toPath()).toString(),
                    file.isDirectory(),
                    file.getName());
        } catch (IOException e) {
            logger.warn("Failed to create ImmutableFile from File!", e);
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public long getSize() {
        return size;
    }

    public FileTime getLastModificationTime() {
        return lastModificationTime;
    }
}
