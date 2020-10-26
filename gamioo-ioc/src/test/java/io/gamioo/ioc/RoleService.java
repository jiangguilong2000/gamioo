package io.gamioo.ioc;

import io.gamioo.ioc.factory.annotation.Autowired;
import io.gamioo.ioc.skill.AbstractSkill;
import io.gamioo.ioc.stereotype.Service;

import java.util.List;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
@Service
public class RoleService {

    @Autowired
    private SkillService skillService;
    @Autowired
    private DebugService debugService;


    public void handleCommand(String value) {
        debugService.handle(value);
    }

    public List<AbstractSkill> getSkillList() {
        return skillService.getSkillList();
    }
}
