package io.gamioo.robot.entity;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class User {
    private String token;
    private long id;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
