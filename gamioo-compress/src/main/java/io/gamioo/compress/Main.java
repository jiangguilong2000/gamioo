package io.gamioo.compress;

import io.gamioo.common.util.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * @author Allen Jiang
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        byte[] array = FileUtils.getByteArrayFromFile("message.txt");
        logger.debug("size:{}", array.length);

    }

}
