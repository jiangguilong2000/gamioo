package com.zvidia.pomelo.protobuf;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jiangzm
 * Date: 13-8-7
 * Time: 下午9:08
 * To change this template use File | Settings | File Templates.
 */
public class Proto implements Serializable {
    private String option;

    private String type;

    private int tag;


    public Proto() {
    }

    public Proto(String option, String type, int tag) {
        this.option = option;
        this.type = type;
        this.tag = tag;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }


}
