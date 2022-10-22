/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.freemanan.testsupport;

import static org.assertj.core.api.Assertions.assertThat;

import javax.servlet.Servlet;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Tests for {@link ModifiedClassPathExtension} overriding entries on the class path.
 *
 * @author Christoph Dreis
 */
@ClassPathOverrides({
    "org.springframework:spring-context:4.1.0.RELEASE",
    "javax.servlet:javax.servlet-api:4.0.1",
    "com.google.code.gson:gson:2.9.1"
})
class ModifiedClassPathExtensionOverridesTests {

    @Test
    void classesAreLoadedFromOverride() {
        assertThat(ApplicationContext.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toString())
                .endsWith("spring-context-4.1.0.RELEASE.jar");

        assertThat(Servlet.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toString())
                .endsWith("javax.servlet-api-4.0.1.jar");
    }

    @Test
    void classesAreLoadedFromTransitiveDependencyOfOverride() {
        assertThat(StringUtils.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toString())
                .endsWith("spring-core-4.1.0.RELEASE.jar");
    }

    @Test
    void classAreLoadedFromOverrideByAddJar() throws ClassNotFoundException {
        assertThat(ClassUtils.isPresent("com.google.gson.Gson", null)).isTrue();
    }
}
