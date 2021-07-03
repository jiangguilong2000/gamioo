package io.gamioo.robot.entity;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class RobotInfo {
    private int group;
    private int area;
    private String ip;
    private int total;
    private int num;
    private int active;

//    public RobotInfo() {
//
//    }

    public RobotInfo(int group, int area, String ip, int total, int num, int active) {
        this.group = group;
        this.area = area;
        this.ip = ip;
        this.total = total;
        this.num = num;
        this.active = active;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String buildKey() {
        return group + "-" + area;
    }
}
