/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.aries.blueprint.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.equinox;
import java.text.SimpleDateFormat;
import java.util.Currency;
import org.apache.aries.blueprint.sample.Bar;
import org.apache.aries.blueprint.sample.Foo;
import org.apache.aries.itest.AbstractIntegrationTest;
import org.apache.aries.itest.RichBundleContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.service.blueprint.container.BlueprintContainer;
import static org.apache.aries.itest.ExtraOptions.*;

@RunWith(JUnit4TestRunner.class)
public class BlueprintAnnotationTest extends AbstractIntegrationTest {

    @Test
    public void test() throws Exception {
        BlueprintContainer blueprintContainer = getBlueprintContainerForBundle(context(), "org.apache.aries.blueprint.sample-annotation");
    
        assertNotNull(blueprintContainer);
    
        Object obj = blueprintContainer.getComponentInstance("bar");
        assertNotNull(obj);
        assertEquals(Bar.class, obj.getClass());
        Bar bar = (Bar) obj;
        assertEquals("Hello FooBar", bar.getValue());
        
        obj = blueprintContainer.getComponentInstance("foo");
        assertNotNull(obj);
        assertEquals(Foo.class, obj.getClass());
        Foo foo = (Foo) obj;
        assertEquals(5, foo.getA());
       // assertEquals(10, foo.getB());
        assertSame(bar, foo.getBar());
        assertEquals(Currency.getInstance("PLN"), foo.getCurrency());
        assertEquals(new SimpleDateFormat("yyyy.MM.dd").parse("2009.04.17"),
                foo.getDate());

        assertTrue(foo.isInitialized());
        assertFalse(foo.isDestroyed());
        
        assertNotNull(blueprintContainer.getComponentInstance("fragment"));
    
        obj = context().getService(Foo.class, null, 5000);
        assertNotNull(obj);
        assertEquals(foo.toString(), obj.toString());
    }

    private BlueprintContainer getBlueprintContainerForBundle(RichBundleContext context, String symbolicName) {
        return context.getService(BlueprintContainer.class, "(osgi.blueprint.container.symbolicname=" + symbolicName + ")");
    }    
    
    @org.ops4j.pax.exam.junit.Configuration
    public static Option[] configuration() {
        return testOptions(
            paxLogging("DEBUG"),

            mavenBundle("org.apache.aries", "org.apache.aries.util"),
            mavenBundle("org.apache.aries.proxy", "org.apache.aries.proxy"),
            mavenBundle("asm", "asm-all"),
            mavenBundle("org.apache.xbean", "xbean-finder"),
            mavenBundle("org.apache.aries.blueprint", "org.apache.aries.blueprint.annotation.api"),
            mavenBundle("org.apache.aries.blueprint", "org.apache.aries.blueprint"),
            mavenBundle("org.apache.aries.blueprint", "org.apache.aries.blueprint.annotation.impl"),
            mavenBundle("org.apache.aries.blueprint", "org.apache.aries.blueprint.sample-annotation"),
            mavenBundle("org.apache.aries.blueprint", "org.apache.aries.blueprint.sample-fragment"),
            mavenBundle("org.osgi", "org.osgi.compendium"),

            equinox().version("3.5.0")
        );
    }

}
