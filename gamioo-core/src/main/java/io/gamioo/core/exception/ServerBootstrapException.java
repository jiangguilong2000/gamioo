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

package io.gamioo.core.exception;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class ServerBootstrapException extends ServiceException{

    public ServerBootstrapException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     * @param message 异常信息
     * @param params
     */
    public ServerBootstrapException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}
