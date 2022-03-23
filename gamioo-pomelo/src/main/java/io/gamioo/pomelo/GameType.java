package io.gamioo.pomelo;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public enum GameType {
    /**
     * 奉化八扣
     */
    COMMON("奉化八扣", "game.fhbk."),
    /**
     * 奉化八扣(多四张)
     */
    DUOSIZHANG("奉化八扣(多四张)", "game.fhbkex."),

    /**
     * 奉化八扣(马牌)
     */
    MAPAI("奉化八扣(马牌)", "game.fhbkm.");

    private String name;
    private String prefix;

    GameType(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
