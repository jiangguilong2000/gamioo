package io.gamioo.ioc.wrapper;

import io.gamioo.ioc.annotation.CommandMapping;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.LongAdder;

/**
 * 指令包装类
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class Command {
    private static final Logger logger = LogManager.getLogger(Command.class);
    private MethodWrapper wrapper;

    private final int code;
    private boolean valid;
    private final boolean print;
    private final boolean login;
    private final boolean cross;
    /**
     * 调用次数累加
     */
    private final LongAdder number = new LongAdder();

    public Command(MethodWrapper wrapper, CommandMapping mapping) {
        this.wrapper = wrapper;
        this.code = mapping.code();
        this.print = mapping.print();
        this.login = mapping.login();
        this.cross = mapping.cross();

    }

    public int getCode() {
        return code;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isPrint() {
        return print;
    }

    public boolean isLogin() {
        return login;
    }

    public boolean isCross() {
        return cross;
    }

    public void increase() {
        number.increment();
    }


    public long getInvokeNumber() {
        return number.longValue();
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

