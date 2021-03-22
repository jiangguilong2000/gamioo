package io.gamioo.benchmark;

/**
 * 性能基准测试内容回调函数
 *
 * @author Allen Jiang
 * @since 1.0
 */
public interface BenchmarkCallback {

    /**
     *
     * @throws Exception 处理业务逻辑
     */
    public void handle() throws Exception;
}