package xyz.nulldev.wls.utils;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nulldev.wls.WebLinkedServer;

import java.io.*;

public class TypeDetector {
    private Detector detector;
    private boolean useSlowDetection = false;

    private Logger logger = LoggerFactory.getLogger(TypeDetector.class);

    public TypeDetector() {
        String useSlowDetectionString
                = WebLinkedServer
                    .getCONFIGURATION()
                    .getConfig()
                    .getProperty("slave.useSlowTypeDetection", "false")
                    .trim();
        detector = new DefaultDetector();
        if(useSlowDetectionString.equalsIgnoreCase("true")
                || useSlowDetectionString.equalsIgnoreCase("yes")) {
            useSlowDetection = true;
        }
    }

    public boolean isUseSlowDetection() {
        return useSlowDetection;
    }

    public void setUseSlowDetection(boolean useSlowDetection) {
        this.useSlowDetection = useSlowDetection;
    }

    public String detectType(File file) {
        InputStream stream = null;
        Metadata metadata = null;

        //Use TikaInputStream if we want slow detection
        if(useSlowDetection) {
            metadata = new Metadata();
            try {
                stream = TikaInputStream.get(file.toPath(), metadata);
            } catch (IOException e) {
                logger.warn("Cannot load slow detection engine, using fast detection!", e);
                metadata = null;
            }
        }

        if(stream == null) {
            try {
                stream = new BufferedInputStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                logger.warn("File was removed while detecting it's type!", e);
                return null;
            }
        }
        if(metadata == null) {
            metadata = new Metadata();
            metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());
        }

        MediaType type;
        try(InputStream theStream = stream) {
            type = detector.detect(theStream, metadata);
        } catch (IOException e) {
            logger.warn("Could not detect type of '" + file.getName() + "'!", e);
            return null;
        }
        return type.toString();
    }
}
