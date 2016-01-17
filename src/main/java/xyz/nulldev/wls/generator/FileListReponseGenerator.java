package xyz.nulldev.wls.generator;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nulldev.wls.io.ImmutableFile;
import xyz.nulldev.wls.utils.IOUtils;
import xyz.nulldev.wls.utils.Utils;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Project: WebLinkedServer
 * Created: 16/01/16
 * Author: nulldev
 */
public class FileListReponseGenerator {
    String dirName;
    String parentPath;
    List<ImmutableFile> fileList;
    SortType sortType;
    SortDirection sortDirection;

    Logger logger = LoggerFactory.getLogger(FileListReponseGenerator.class);

    /**
     * Icons and stuff
     */
    static String IMG_ICN_FILE = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAA9QTFRFAAAA5PX6yez1/////////SHUhAAAAAV0Uk5T/////wD7tg5TAAAAQUlEQVR42mJgQQMMIAwFCAEmJkZGRgZmBnQBsAhEAKieEaoJoQKkCLcAxBZGyrQwoWuBiDCg+QXuMAzfogCAAAMAPGcCZJ/5Pw0AAAAASUVORK5CYII=";
    static String IMG_ICN_DIR = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAABJQTFRF//IAAAAA8ucA////ysAA////eQEvdgAAAAZ0Uk5T//////8As7+kvwAAADtJREFUeNpiYEUDDKyMyAAswMLExAAFzIyMDCgKGJmBAkgKGOgrwIQsAHYpTAjkUlS/gAQwfIsGAAIMAN7QAc8Ckh4aAAAAAElFTkSuQmCC";
    static String IMG_ICN_ASC = "iVBORw0KGgoAAAANSUhEUgAAAAcAAAAECAMAAAB1GNVPAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAAZQTFRF////////VXz1bAAAAAJ0Uk5T/wDltzBKAAAAGklEQVR42mJgZGRkgGAGBhABohigJBAABBgAATAADUnnWMkAAAAASUVORK5CYII=";
    static String IMG_ICN_DSC = "iVBORw0KGgoAAAANSUhEUgAAAAcAAAAECAMAAAB1GNVPAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAAZQTFRF////////VXz1bAAAAAJ0Uk5T/wDltzBKAAAAF0lEQVR42mJggAJGKMkIYjCCaDAGCDAAAJAADcpaiWkAAAAASUVORK5CYII=";

    public FileListReponseGenerator(String dirName, String parentPath, List<ImmutableFile> fileList, SortType sortType, SortDirection sortDirection) {
        this.dirName = dirName;
        this.parentPath = parentPath;
        if(!this.parentPath.startsWith("/")) {
            this.parentPath = "/" + this.parentPath;
        }
        this.fileList = fileList;
        this.sortType = sortType;
        this.sortDirection = sortDirection;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public List<ImmutableFile> getFileList() {
        return fileList;
    }

    public void setFileList(List<ImmutableFile> fileList) {
        this.fileList = fileList;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }

    public enum SortType {
        NAME("Name", (Comparator<ImmutableFile>) (o1, o2) -> o1.getName().compareTo(o2.getName())),
        SIZE("Size", (Comparator<ImmutableFile>) (o1, o2) -> {
            if (o1.getSize() == -1 && o2.getSize() == -1) {
                return 0;
            } else if (o1.getSize() == -1) {
                return -1;
            } else if (o2.getSize() == -1) {
                return 1;
            } else {
                return Long.compare(o1.getSize(), o2.getSize());
            }
        }),
        DATE("Modified", (Comparator<ImmutableFile>) (o1, o2) -> {
            if (o1.getLastModificationTime() == null && o2.getLastModificationTime() == null) {
                return 0;
            } else if (o1.getLastModificationTime() == null) {
                return -1;
            } else if (o2.getLastModificationTime() == null) {
                return 1;
            } else {
                return o1.getLastModificationTime().compareTo(o2.getLastModificationTime());
            }
        });

        private String display;
        private Comparator<ImmutableFile> comparator;

        SortType(String display, Comparator<ImmutableFile> comparator) {
            this.display = display;
            this.comparator = comparator;
        }

        public String getDisplay() {
            return display;
        }

        public Comparator<ImmutableFile> getComparator() {
            return comparator;
        }
    }

    public enum SortDirection {
        ASCENDING(IMG_ICN_ASC),
        DESCENDING(IMG_ICN_DSC);

        private String icon;

        SortDirection(String icon) {
            this.icon = icon;
        }

        public String getIcon() {
            return icon;
        }
    }

    public String generate() {
        StringBuilder output = new StringBuilder();
        String core;
        String listing;
        String item;
        try {
            core = IOUtils.getResourceAsString("/index.template.html");
            listing = IOUtils.getResourceAsString("/index-listing.template.html");
            item = IOUtils.getResourceAsString("/index-element.template.html");
        } catch (IOException e) {
            logger.error("Failed to read templates!", e);
            return null;
        }
        if(fileList == null || fileList.isEmpty()) {
            listing = "<p>This directory is empty!</p>";
        } else {
            //Generate header
            StringBuilder header = new StringBuilder();
            for(SortType curSortType : SortType.values()) {
                header.append("<th><a href=\"");
                header.append("?sort=").append(curSortType);
                String extra = "";
                if(curSortType.equals(sortType)) {
                    header.append("&amp;direction=");
                    if(sortDirection.equals(SortDirection.ASCENDING)) {
                        header.append(SortDirection.DESCENDING.name());
                    } else if(sortDirection.equals(SortDirection.DESCENDING)) {
                        header.append(SortDirection.ASCENDING.name());
                    } else {
                        logger.error("Unknown sort type!");
                        throw new RuntimeException("Unknown sort type!");
                    }
                    extra = " <img src=\"data:image/png;base64," + sortDirection.getIcon() + "\" alt=\"\" />";
                }
                header.append("\">");
                header.append(curSortType.getDisplay());
                header.append(extra);
                header.append("</a></th>");
            }
            StringBuilder body = new StringBuilder();
            List<ImmutableFile> tempList = new ArrayList<>();
            tempList.addAll(fileList);
            Collections.sort(tempList, sortType.getComparator());
            if(sortDirection.equals(SortDirection.DESCENDING)) {
                tempList = Lists.reverse(tempList);
            }
            int id = 0;
            for(ImmutableFile file : tempList) {
                id++;
                String cssClass = (id & 1) == 0 ? "even" : "odd";
                String icon = file.isDirectory() ? IMG_ICN_DIR : IMG_ICN_FILE;
                String type = file.isDirectory() ? "Directory" : "File";
                String link = "/" + file.getRelativePath();
                String name = file.getName();
                String size = file.isDirectory() ? "-" : Utils.formatSize(file.getSize());
                String date = DateTimeFormatter.RFC_1123_DATE_TIME
                        .format(ZonedDateTime.ofInstant(file.getLastModificationTime().toInstant(),
                        TimeZone.getDefault().toZoneId()));
                body.append(item.replace("%CLASS%", cssClass)
                        .replace("%ICON%", icon)
                        .replace("%TYPE%", type)
                        .replace("%LINK%", link)
                        .replace("%FILENAME%", name)
                        .replace("%SIZE%", size)
                        .replace("%DATE%", date));
            }
            listing = listing.replace("%HEADINGS%", header.toString())
                    .replace("%BODY%", body);
        }

        output.append(core);
        output = new StringBuilder(output.toString()
                .replace("%DIRNAME%", dirName)
                .replace("%PARENTPATH%", parentPath)
                .replace("%LISTING%", listing));
        return output.toString();
    }
}