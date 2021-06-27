package io.gamioo.redis;

/**
 * @author Allen
 */
public class RedisConstant {
    // Redis库的类型
    /**
     * 全局库
     */
    public final static int REDIS_TYPE_GLOBAL = 1;
    /**
     * 本地库
     */
    public final static int REDIS_TYPE_LOCAL = 2;


    /**
     * 全局CDN
     */
    public static final String KEY_GLOBAL_CDN = "globalcdn";
    public static final String KEY_CDN = "cdn";

    public static final String KEY_CDN_DIR = "cdn_dir";

    public static final String HISTORY_CDN = "history_cdn";

    public static final String HISTORY_CDN_URL = "url";
    public static final String HISTORY_CDN_SUB_DOMAIN = "sub_domain";
    public static final String HISTORY_CDN_ROOT_DOMAIN = "root_domain";
    public static final String HISTORY_CDN_BACKUP_URL = "backup_url";

    public static final String KEY_PLATFORMS = "platforms";

    public static final String KEY_ONLINE = "online";

    public static final String KEY_NOTICE_LIST = "notice";

    public static final String KEY_GAME_LIST = "game_config";
    /**
     * 用户信息
     */
    public static final String KEY_USER = "u";
    /**
     * 用户ID,用于自增
     */
    public static final String KEY_USER_ID = "user_id";
    /**
     * 用户名字和ID映射
     */
    public static final String KEY_USER_ID_NAME = "u3";
    /**
     * 服务器信息
     */
    public static final String KEY_SERVER_INFO = "server_info";

    /**
     * 样本信息
     */
    public static final String KEY_ROBOT = "robot";

    /**
     * 服务器
     */
    public static final String KEY_TARGET = "target";
    // 频道
    /**
     * 服务器增加频道
     */
    public static final String CHANNEL_ADD_SERVER = "channel-add-server";
    public static final String CHANNEL_ADD_GLOBAL = "channel-add-global";

    /**
     * 服务器删除频道
     */
    public static final String CHANNEL_REMOVE_SERVER = "channel-remove-server";
    public static final String CHANNEL_REMOVE_GLOBAL = "channel-remove-global";

    /**
     * 跨服服务器频道
     */
    public static final String CHANNEL_SERVER_LOBBY = "channel-lobby";
    /**
     * 统计频道
     */
    public static final String CHANNEL_STATISTIC = "channel-stat";

    /**
     * 监控频道
     */
    public static final String CHANNEL_MONITOR = "channel-monitor";

    /**
     * 代码热更新
     */
    public static final String CHANNEL_HOT_CODE = "channel-hot-code";
    /**
     * 平台更新
     */
    public static final String CHANNEL_PLATFORMS = "channel-platforms";
    /**
     * 全局CDN频道
     */
    public static final String CHANNEL_GLOBAL_CDN = "channel-global-cdn";
    /**
     * CDN频道
     */
    public static final String CHANNEL_CDN = "channel-cdn";
    /**
     * 服务器频道
     */
    public static final String CHANNEL_SERVER = "channel-server-";
    /**
     * 服务器在线人数频道
     */
    public static final String CHANNEL_SERVER_ONLINE = "channel-online";
    /**
     * 服务器在线人数
     */
    public static final String SERVER_ONLINE = "server_online";
    /**
     * 游戏类型在线人数
     */
    public static final String GAME_TYPE_ONLINE = "game_type_online";

    /**
     * 消息类型和消息的分隔符
     */
    public static final String CHANNEL_SEPARATOR = "@";

    public static String getServerChannel(String serverType, int serverId) {
        return RedisConstant.CHANNEL_SERVER + serverType + "-" + serverId;
    }


}
