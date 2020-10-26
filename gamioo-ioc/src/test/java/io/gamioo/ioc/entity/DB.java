package io.gamioo.ioc.entity;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class DB {
    private String jdbcUser;
    private String jdbcUrl;
    private String jdbcPassword;

    public String getJdbcUser() {
        return jdbcUser;
    }

    public void setJdbcUser(String jdbcUser) {
        this.jdbcUser = jdbcUser;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public void setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }
}
