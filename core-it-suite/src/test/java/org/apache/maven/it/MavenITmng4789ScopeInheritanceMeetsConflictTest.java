package org.apache.maven.it;

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

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * This is a test set for <a href="http://jira.codehaus.org/browse/MNG-4789">MNG-4789</a>.
 * 
 * @author Benjamin Bentmann
 */
public class MavenITmng4789ScopeInheritanceMeetsConflictTest
    extends AbstractMavenIntegrationTestCase
{

    public MavenITmng4789ScopeInheritanceMeetsConflictTest()
    {
        super( "[2.0.3,3.0-alpha-1),[3.0-beta-4,)" );
    }

    /**
     * Test that scope inheritance considers the effective scope of parent nodes as enforced by direct dependency
     * declarations.
     */
    public void testit()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-4789" );

        Verifier verifier = newVerifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteArtifacts( "org.apache.maven.its.mng4789" );
        verifier.deleteDirectory( "target" );
        verifier.getCliOptions().add( "-s" );
        verifier.getCliOptions().add( "settings.xml" );
        verifier.filterFile( "settings-template.xml", "settings.xml", "UTF-8", verifier.newDefaultFilterProperties() );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        List compile = verifier.loadLines( "target/compile.txt", "UTF-8" );
        assertFalse( compile.toString(), compile.contains( "a-0.1.jar" ) );
        assertTrue( compile.toString(), compile.contains( "b-0.1.jar" ) );
        assertFalse( compile.toString(), compile.contains( "x-0.1.jar" ) );

        List runtime = verifier.loadLines( "target/runtime.txt", "UTF-8" );
        assertFalse( runtime.toString(), runtime.contains( "a-0.1.jar" ) );
        assertTrue( runtime.toString(), runtime.contains( "b-0.1.jar" ) );
        assertFalse( runtime.toString(), runtime.contains( "x-0.1.jar" ) );

        List test = verifier.loadLines( "target/test.txt", "UTF-8" );
        assertTrue( test.toString(), test.contains( "a-0.1.jar" ) );
        assertTrue( test.toString(), test.contains( "b-0.1.jar" ) );
        assertTrue( test.toString(), test.contains( "x-0.1.jar" ) );
    }

}
