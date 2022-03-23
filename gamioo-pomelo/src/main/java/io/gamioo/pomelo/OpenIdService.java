package io.gamioo.pomelo;


import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 如果暴力不是为了杀戮
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class OpenIdService {
    private static final Logger LOGGER = LogManager.getLogger(OpenIdService.class);
    private static final char[] special = "-_".toCharArray();
    public static final char[] num = "0123456789".toCharArray();
    public static final char[] abc = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    public static final char[] ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static final char[][] array = new char[][]{abc, ABC, num};
//    public static final char[][] after_special = new char[][]{abc, ABC, num};
//    public static final char[][] after_num = new char[][]{abc, ABC};

    private static final String prefix = "oxmtuw";
    private static final String postfix = "@ll2020";
    private int index;

    public String generateId() {
        StringBuilder ret = new StringBuilder();
        ret.append(prefix);

        //要不要有下划线和中划线
        int special = this.getSpecialIndex();

        //  int[] typeArray = new int[22];
        for (int i = 0; i < 22; i++) {
            if (special == i) {
                String value = getSpecailValue();
                ret.append(value);
            } else {
                index = this.getIndex(index);
                //    typeArray[i] = index;
                String value = this.getValue(index);
                ret.append(value);
            }

        }
        ret.append(postfix);

        return ret.toString();
    }

    private int getSpecialIndex() {
        int ret = -1;
        if (RandomUtils.nextBoolean()) {
            ret = RandomUtils.nextInt(0, 22);
        }
        return ret;
    }

    private String getValue(int type) {
        char[] elements = array[index];
        return RandomStringUtils.random(1, elements);
        //  RandomStringUtils.random(1, 0, 0, true, true, elements);
    }

    private String getSpecailValue() {
        return RandomStringUtils.random(1, special);
        //  RandomStringUtils.random(1, 0, 0, true, true, elements);
    }

    /**
     * 通过前面的值获得后面的值的类型
     */
    private int getIndex(int index) {
        if (index == 2) {
            return RandomUtils.nextInt(0, 2);
        } else {
            return RandomUtils.nextInt(0, 3);
        }
    }

    public static void main(String[] args) {
        OpenIdService service = new OpenIdService();
        for (int i = 0; i < 100; i++) {
            String id = service.generateId();
            System.out.println(id);
            // LOGGER.debug(id);
        }

        //   Thread.sleep(5000);
    }

}
