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

package io.gamioo.ioc.factory.xml;

import io.gamioo.ioc.io.Resource;
import io.gamioo.ioc.io.ResourceLoader;
import io.gamioo.ioc.io.UrlFileResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class XmlResourceLoader  implements ResourceLoader {

    /**
     * Resolve the given location pattern into Resource objects.
     * <p>Overlapping resource entries that point to the same physical
     * resource should be avoided, as far as possible. The result should
     * have set semantics.
     *
     * @param location the location pattern to resolve
     * @return the corresponding Resource objects
     * @throws IOException in case of I/O errors
     */
    @Override
    public List<Resource> getResourceList(String location) throws IOException {
        List<Resource> ret=new ArrayList<>();
       // URL url = ClassUtils.getDefaultClassLoader().getResource(location);
        UrlFileResource resource=new UrlFileResource(location);
        ret.add(resource);
        return  ret;
    }
}
