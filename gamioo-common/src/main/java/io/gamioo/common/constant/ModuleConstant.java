package io.gamioo.common.constant;

import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Allen Jiang
 */
public class ModuleConstant {
    @Describe(name = "common", description = "通用模块")
    public static final int COMMON = 0;
    @Describe(name = "user", description = "用户模块")
    public static final int USER = 1;
    @Describe(name = "building", description = "建筑模块")
    public static final int BUILDING = 2;
    @Describe(name = "item", description = "道具模块")
    public static final int ITEM = 3;


    private static List<String> moduleList = new ArrayList<>();

    /**
     * 获取模块列表
     *
     * @return 返回模块列表
     */
    public static List<String> getModuleList() {
        if (CollectionUtils.isEmpty(moduleList)) {
            Field[] list = ModuleConstant.class.getDeclaredFields();
            for (Field e : list) {
                Describe describe = e.getAnnotation(Describe.class);
                if (describe != null) {
                    moduleList.add(describe.name());
                }
            }
        }
        return moduleList;

    }
}