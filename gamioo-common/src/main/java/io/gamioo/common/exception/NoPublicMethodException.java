/*
 * Copyright 2015-2020 Gamioo Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gamioo.common.exception;

/**
 * 在找不到指定Public方法时抛出.
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class NoPublicMethodException extends RuntimeException {
    private static final long serialVersionUID = -4256184544259394170L;

    public NoPublicMethodException(String msg) {
        super(msg);
    }
}
