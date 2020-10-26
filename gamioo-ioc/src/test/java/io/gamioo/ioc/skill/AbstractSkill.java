package io.gamioo.ioc.skill;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public  abstract class AbstractSkill {

    public abstract void init();

    public abstract  void handle();

    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
