package io.gamioo.poker.protocol;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class LoginDTO {
    @JSONField(name = "token")
    private String token;
    @JSONField(name = "roomid")
    private String roomId;
    @JSONField(name = "time")
    private long time;
    @JSONField(name = "sign")
    private String sign;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
//42["login",{"token":"930e424586d3670123cfe191b744d4d0","roomid":"616000","time":1638552497052,"sign":"9f7911eab9dd32309a5b3faef9a59d20"}]
}
