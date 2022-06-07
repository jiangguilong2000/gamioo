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

package io.gamioo.redis.zset.generic;

/**
 * 存放一些常用的{@link ScoreHandler}实现。
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/11/6
 */
public class ScoreHandlers {

    private ScoreHandlers() {

    }

    /**
     * @return Long类型的score处理器
     */
    public static ScoreHandler<Long> longScoreHandler() {
        return longScoreHandler(false);
    }

    /**
     * @param desc 是否降序
     * @return Long类型的score处理器
     */
    public static ScoreHandler<Long> longScoreHandler(boolean desc) {
        return desc ? DescLongScoreHandler.INSTANCE : LongScoreHandler.INSTANCE;
    }

    /**
     * Long类型的score处理器 - 升序
     */
    private static class LongScoreHandler implements ScoreHandler<Long> {

        private static final LongScoreHandler INSTANCE = new LongScoreHandler();

        private LongScoreHandler() {

        }

        @Override
        public int compare(Long o1, Long o2) {
            return o1.compareTo(o2);
        }

        @Override
        public Long sum(Long oldScore, Long increment) {
            return oldScore + increment;
        }
    }

    /**
     * Long类型的score处理器 - 降序
     */
    private static class DescLongScoreHandler implements ScoreHandler<Long> {

        private static final DescLongScoreHandler INSTANCE = new DescLongScoreHandler();

        private DescLongScoreHandler() {

        }

        @Override
        public int compare(Long o1, Long o2) {
            // 逆序
            return o2.compareTo(o1);
        }

        @Override
        public Long sum(Long oldScore, Long increment) {
            return oldScore + increment;
        }
    }
}
