package io.gamioo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class MainT {
    private static final Logger logger = LogManager.getLogger(MainT.class);

    public static void main(String[] args) {
        String packager = MainT.class.getPackage().getName();
        String[] packages = Arrays.asList(packager, "io.gamioo").toArray(new String[]{});
        logger.debug("init ioc, packages={}", packager);
    }


}
