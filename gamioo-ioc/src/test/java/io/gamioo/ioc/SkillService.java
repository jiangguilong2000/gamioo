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
public class SkillService {

    @Autowired
    private List<AbstractSkill> skillList;

    public List<AbstractSkill> getSkillList() {
        return skillList;
    }


}
