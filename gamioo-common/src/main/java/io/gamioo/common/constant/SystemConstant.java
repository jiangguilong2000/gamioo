package io.gamioo.common.constant;

/**
 * @author Allen Jiang
 */
public class SystemConstant {
    // 服务器类型
    /**
     * 游戏服务器
     */
    public final static String SERVER_TYPE_GAME = "game";
    /**
     * 全局服务器
     */
    public final static String SERVER_TYPE_GLOBAL = "global";

    /**
     * 日志服务器
     */
    public final static String SERVER_TYPE_LOGGER = "logger";
    /**
     * 充值服务器
     */
    public final static String SERVER_TYPE_CHARGE = "charge";
    /**
     * 网关服务器
     */
    public final static String SERVER_TYPE_GATE = "gate";
    /**
     * admin服务器
     */
    public final static String SERVER_TYPE_ADMIN = "admin";
    /**
     * 目录服务器
     */
    public final static String SERVER_TYPE_DIRECTORY = "directory";

    public static final String LOCALHOST = "127.0.0.1";

//    public static String getConfig(String gameType) {
//        return gameType + "_config";
//    }

    // 服务器状态
    /**
     * 服务器处于正常运行状态
     */
    public final static int SERVER_STATUS_OPENING = 0;
    /**
     * 服务器处于运维维护状态,只能允许GM进来
     */
    public final static int SERVER_STATUS_MAINTAIN = 1;
    /**
     * 服务器处于停服状态
     */
    public final static int SERVER_STATUS_STOPED = 2;

    // 系统
    /**
     * 系统ID
     */
    public final static int SYSTEM_ID = 0;
    /**
     * 系统名
     */
    public final static String SYSTEM_NAME = "系统";
    /**
     * 系统
     */
    public final static String NAME_SYS = "SYSTEM";

    // 游戏状态
    /**
     * 敬请期待
     */
    public final static int GAME_STATUS_PENDING = 0;
    /**
     * 正常对外
     */
    public final static int GAME_STATUS_OPENING = 1;
    /**
     * 正在维护
     */
    public final static int GAME_STATUS_MAINTAIN = 2;

    /**
     * 对20毫秒以下的处理值进行监控
     */
    public final static int THRESHOLD_DELAY = 20;

    /**
     * 服务器类型转化
     *
     * @param type 服务器类型
     * @return 返回服务器数字类型
     */
    public static int getServerType(String type) {
        switch (type) {
            case SERVER_TYPE_DIRECTORY: {
                return 1;

            }
            case SERVER_TYPE_GAME: {
                return 2;
            }
            default: {
                return 0;
            }
        }

    }
}
