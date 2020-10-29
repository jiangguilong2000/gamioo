package io.gamioo.ioc.action;

import io.gamioo.ioc.annotation.CommandMapping;
import io.gamioo.ioc.stereotype.Controller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
@Controller
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);
    @CommandMapping(code=10001,cross = true,print = false,login = false)
    public void login(Object session,String message){
        logger.debug("object={},message={}",session,message);
    }
}
