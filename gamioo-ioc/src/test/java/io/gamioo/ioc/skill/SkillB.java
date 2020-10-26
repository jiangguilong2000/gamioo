package io.gamioo.ioc.skill;

import io.gamioo.ioc.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
@Component(name="B")
public class SkillB extends AbstractSkill{
    private static final Logger logger = LogManager.getLogger(SkillB.class);
    @Override
    @PostConstruct
    public void init() {
        logger.debug("{} init",this.getClass().getSimpleName());
    }

    @Override
    public void handle() {
        logger.debug("{} handle",this.getClass().getSimpleName());
    }
}
