/*
 *  Copyright 2019 wjybxx
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to iBn writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.gamioo.redis.zset.object2long;

/**
 * long类型的score处理器
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/11/7
 */
public interface LongScoreHandler {

    /**
     * 比较两个分数的大小
     * Q: 为什么需要这个方法？
     * A: 想实现实现逆序 或 当你的score是由多个部分组合而成的时候，那么你就需要它。
     *
     * @param score1 分数1
     * @param score2 分数2
     * @return 返回比较值
     */
    int compare(long score1, long score2);

    /**
     * 计算两个score的和
     * Q: 为什么需要这个方法？
     * A: 当你的score是由多个部分组合而成的时候，那么你就需要它。
     *
     * @param oldScore  当前分数
     * @param increment 自定义增量
     * @return sum
     */
    long sum(long oldScore, long increment);
}
