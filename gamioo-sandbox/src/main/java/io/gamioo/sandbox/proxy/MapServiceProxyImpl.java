package io.gamioo.sandbox.proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapServiceProxyImpl implements MapServiceProxy {
    private static final Logger LOGGER = LogManager.getLogger(MapServiceProxyImpl.class);

    @Override
    public void march(long roleId, int marchId) {
        LOGGER.debug("roleId={},marchId={}", roleId, marchId);
    }
}
