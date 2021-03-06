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

import java.io.File;
import java.net.InetAddress;
import java.util.Properties;

import org.apache.maven.it.util.ResourceExtractor;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;

/**
 * This is a test set for <a href="http://jira.codehaus.org/browse/MNG-2387">MNG-2387</a>.
 * 
 * @author Brett Porter
 * @version $Id$
 */
public class MavenITmng2387InactiveProxyTest
    extends AbstractMavenIntegrationTestCase
{
    private Server server;

    private int port;

    private Server proxyServer;

    private int proxyPort;

    private File testDir;

    public MavenITmng2387InactiveProxyTest()
    {
        super( "[2.0.11,2.1.0-M1),[2.1.0,)" ); // 2.0.11+, 2.1.0+
    }

    public void setUp()
        throws Exception
    {
        testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-2387" );

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase( new File( testDir, "repo" ).getAbsolutePath() );

        HandlerList handlers = new HandlerList();
        handlers.setHandlers( new Handler[] { resourceHandler, new DefaultHandler() } );

        server = new Server( 0 );
        server.setHandler( handlers );
        server.start();

        port = server.getConnectors()[0].getLocalPort();

        resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase( new File( testDir, "proxy" ).getAbsolutePath() );

        handlers = new HandlerList();
        handlers.setHandlers( new Handler[] { resourceHandler, new DefaultHandler() } );

        proxyServer = new Server( 0 );
        proxyServer.setHandler( handlers );
        proxyServer.start();

        proxyPort = proxyServer.getConnectors()[0].getLocalPort();
    }

    protected void tearDown()
        throws Exception
    {
        super.tearDown();

        if ( server != null )
        {
            server.stop();
            server = null;
        }
        if ( proxyServer != null )
        {
            proxyServer.stop();
            proxyServer = null;
        }
    }

    /**
     * Test that no proxy is used if none of the configured proxies is actually set as active.
     */
    public void testit()
        throws Exception
    {
        Verifier verifier = newVerifier( testDir.getAbsolutePath() );
        
        Properties properties = verifier.newDefaultFilterProperties();        
        properties.setProperty( "@host@", InetAddress.getLocalHost().getCanonicalHostName() );
        properties.setProperty( "@port@", Integer.toString( port ) );
        properties.setProperty( "@proxyPort@", Integer.toString( proxyPort ) );
        verifier.filterFile( "settings-template.xml", "settings.xml", "UTF-8", properties );

        verifier.setAutoclean( false );
        verifier.deleteArtifacts( "org.apache.maven.its.mng2387" );
        verifier.getCliOptions().add( "--settings" );
        verifier.getCliOptions().add( "settings.xml" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertArtifactPresent( "org.apache.maven.its.mng2387", "a", "0.1", "jar" );
    }

}
