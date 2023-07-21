package io.gamioo.sandbox;

import com.github.houbb.data.factory.api.annotation.DataFactory;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 技能回包
 * @author Allen Jiang
 */
public class SkillFire_S2C_Msg {
    @DataFactory(min = 1000000000, max = Integer.MAX_VALUE)
    private  Long attackerId;
    private SkillCategory skillCategory;//技能类型(大类)
    @DataFactory(min = 0, max = 100)
    private int index;//设置连招索引
    @DataFactory(min = 0, max = 5)
    private List<HarmDTO> harmList=new ArrayList<>();

    private List<Long> param1=new ArrayList<>();

//    private Map<String, String> store=new HashMap<>();

//    public Map<String, String> getStore() {
//        return store;
//    }
//
//    public void setStore(Map<String, String> store) {
//        this.store = store;
//    }

    public Long getAttackerId() {
        return attackerId;
    }

    public void setAttackerId(Long attackerId) {
        this.attackerId = attackerId;
    }

    public SkillCategory getSkillCategory() {
        return skillCategory;
    }

    public void setSkillCategory(SkillCategory skillCategory) {
        this.skillCategory = skillCategory;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<HarmDTO> getHarmList() {
        return harmList;
    }

    public void setHarmList(List<HarmDTO> harmList) {
        this.harmList = harmList;
    }

    public List<Long> getParam1() {
        return param1;
    }

    public void setParam1(List<Long> param1) {
        this.param1 = param1;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
