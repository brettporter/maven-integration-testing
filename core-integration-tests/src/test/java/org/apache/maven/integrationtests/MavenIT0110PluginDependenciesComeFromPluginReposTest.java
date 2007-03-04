package org.apache.maven.integrationtests;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

import java.io.File;

public class MavenIT0110PluginDependenciesComeFromPluginReposTest
    extends AbstractMavenIntegrationTestCase
{
    public void testit0110()
        throws Exception
    {
        File testDir =
            ResourceExtractor.simpleExtractResources( getClass(), "/it0110-pluginDependenciesComeFromPluginRepos" );
        Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        verifier.deleteArtifact( "org.apache.maven.it", "mail", "1.3.2", "jar" );        
        verifier.executeGoal( "clean" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

    }
}