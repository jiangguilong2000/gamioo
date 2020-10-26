package io.gamioo.ioc.map;

import io.gamioo.ioc.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
@Component(name="-- add item")
public class AddItemCommand implements Command {
    private static final Logger logger = LogManager.getLogger(AddItemCommand.class);
    @Override
    public void execute() {
        logger.debug("{} execute",this.getClass().getSimpleName());
    }
}
