/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.tools.wsdlto.frontend.reqwest;

import java.util.List;
import java.util.Map;

import org.apache.cxf.tools.common.FrontEndGenerator;
import org.apache.cxf.tools.common.Processor;
import org.apache.cxf.tools.plugin.FrontEnd;
import org.apache.cxf.tools.plugin.Generator;
import org.apache.cxf.tools.plugin.Plugin;
import org.apache.cxf.tools.wsdlto.core.AbstractWSDLBuilder;
import org.apache.cxf.tools.wsdlto.core.FrontEndProfile;
import org.apache.cxf.tools.wsdlto.core.PluginLoader;
import org.apache.cxf.tools.wsdlto.frontend.reqwest.generators.AntGenerator;
import org.apache.cxf.tools.wsdlto.frontend.reqwest.generators.ImplGenerator;
import org.apache.cxf.tools.wsdlto.frontend.reqwest.processor.WSDLToRustProcessor;
import org.apache.cxf.tools.wsdlto.frontend.reqwest.wsdl11.ReqwestDefinitionBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ReqwestProfileTest {

    @Test
    public void testLoadPlugins() {
        PluginLoader loader = PluginLoader.getInstance();
        assertNotNull(loader);

        loader.loadPlugin("/org/apache/cxf/tools/wsdlto/frontend/reqwest/reqwest-plugin.xml");

        assertEquals(3, loader.getPlugins().size());

        Plugin plugin = null;
        for (Plugin p : loader.getPlugins().values()) {
            if (p.getName().contains("reqwest")) {
                plugin = p;
            }
        }
        assertNotNull(plugin);
        assertEquals("tools-reqwest-frontend", plugin.getName());
        assertEquals("2.0", plugin.getVersion());
        assertEquals("apache cxf", plugin.getProvider());

        Map<String, FrontEnd> frontends = loader.getFrontEnds();
        assertNotNull(frontends);
        assertEquals(1, frontends.size());

        FrontEnd frontend = getFrontEnd(frontends, 0);
        assertEquals("reqwest", frontend.getName());
        assertEquals("org.apache.cxf.tools.wsdlto.frontend.reqwest", frontend.getPackage());
        assertEquals("ReqwestProfile", frontend.getProfile());
        assertNotNull(frontend.getGenerators());
        assertNotNull(frontend.getGenerators().getGenerator());
        assertEquals(2, frontend.getGenerators().getGenerator().size());
        assertEquals("AntGenerator", getGenerator(frontend, 0).getName());
        assertEquals("ImplGenerator", getGenerator(frontend, 1).getName());

        FrontEndProfile profile = loader.getFrontEndProfile("reqwest");
        assertNotNull(profile);
        List<FrontEndGenerator> generators = profile.getGenerators();
        assertNotNull(generators);
        assertEquals(2, generators.size());
        assertTrue(generators.get(0) instanceof AntGenerator);
        assertTrue(generators.get(1) instanceof ImplGenerator);
        Processor processor = profile.getProcessor();
        assertNotNull(processor);
        assertTrue(processor instanceof WSDLToRustProcessor);

        AbstractWSDLBuilder builder = profile.getWSDLBuilder();
        assertNotNull(builder);
        assertTrue(builder instanceof ReqwestDefinitionBuilder);

        Class<?> container = profile.getContainerClass();
        assertEquals(container, ReqwestContainer.class);
        assertEquals("/org/apache/cxf/tools/wsdlto/frontend/reqwest/reqwest-toolspec.xml", profile.getToolspec());
    }

    protected Generator getGenerator(FrontEnd frontend, int index) {
        return frontend.getGenerators().getGenerator().get(index);
    }

    protected FrontEnd getFrontEnd(Map<String, FrontEnd> frontends, int index) {
        int size = frontends.size();
        return frontends.values().toArray(new FrontEnd[size])[index];
    }

    protected Plugin getPlugin(PluginLoader loader, int index) {
        int size = loader.getPlugins().size();
        return loader.getPlugins().values().toArray(new Plugin[size])[index];
    }
}