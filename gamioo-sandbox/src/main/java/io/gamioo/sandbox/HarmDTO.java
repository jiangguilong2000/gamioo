package io.gamioo.sandbox;

import com.github.houbb.data.factory.api.annotation.DataFactory;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Allen Jiang
 */
public class HarmDTO {
    @DataFactory(min = 1000000000, max = Integer.MAX_VALUE)
    private Long  targetId;//受伤者
    @DataFactory(min = 1, max = 100)
    private int type;// 伤害类型

    @DataFactory(min = 0, max = 99999)
    private float value;//伤害值
    private boolean dead; //是否致死
    @DataFactory(min = 0, max = 99999)
    private long real; //真实伤害
    @DataFactory(min = 0, max = 9999999)
    private float maxHp;//最大血量
    @DataFactory(min = 0, max = 9999999)
    private float curHp;//当前血量

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public long getReal() {
        return real;
    }

    public void setReal(long real) {
        this.real = real;
    }

    public float getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(float maxHp) {
        this.maxHp = maxHp;
    }

    public float getCurHp() {
        return curHp;
    }

    public void setCurHp(float curHp) {
        this.curHp = curHp;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
