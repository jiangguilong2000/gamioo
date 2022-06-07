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
 * Score范围区间
 *
 * @author wjybxx
 * @version 1.0
 * date - 2019/11/7
 */
public class ScoreRangeSpec<S> {

    /**
     * 起始分数 - 最高分或最低分
     */
    private final S start;
    /**
     * 是否去除起始分数
     * exclusive
     */
    private final boolean startEx;
    /**
     * 截止分数 - 最高分或最低分
     */
    private final S end;
    /**
     * 是否去除最高分
     * exclusive
     */
    private final boolean endEx;

    public ScoreRangeSpec(S start, S end) {
        this(start, false, end, false);
    }

    public ScoreRangeSpec(S start, boolean startEx, S end, boolean endEx) {
        this.start = start;
        this.startEx = startEx;
        this.end = end;
        this.endEx = endEx;
    }


    public S getStart() {
        return start;
    }

    public boolean isStartEx() {
        return startEx;
    }

    public S getEnd() {
        return end;
    }

    public boolean isEndEx() {
        return endEx;
    }
}
