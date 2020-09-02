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

package io.gamioo.ioc.io;

import org.apache.commons.lang3.NotImplementedException;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public abstract  class AbstractResource implements Resource{
    protected final String name;

    protected AbstractResource(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getClassName() {
        throw new NotImplementedException(this.getClass().getName());
    }

    @Override
    public String toString() {
        return "AbstractResource [name=" + name + "]";
    }
}
