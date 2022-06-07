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

import java.util.Comparator;

/**
 * 泛型化的分数处理器
 *
 * <b>Note:</b>
 * 1. score对象必须实现为不可变，一定不可以修改里面的内容。
 * 2. {@link #sum(Object, Object)}必须返回一个新对象。
 * 3. 不要在score对象中存储杂七杂八的属性，如果想存储一些额外的数据，请存储在key中。
 *
 * @param <T> the type of score
 * @author wjybxx
 * @version 1.0
 * date - 2019/11/6
 */
public interface ScoreHandler<T> extends Comparator<T> {

    /**
     * 比较两个score的大小。
     *
     * @param o1 score
     * @param o2 score
     * @return 0表示相等
     */
    @Override
    int compare(T o1, T o2);

    /**
     * 计算两个score的和
     *
     * @param oldScore  当前分数
     * @param increment 增量
     * @return newInstance
     */
    T sum(T oldScore, T increment);

}
