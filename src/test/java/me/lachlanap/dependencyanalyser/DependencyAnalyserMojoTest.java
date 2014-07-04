/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser;

import java.io.File;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

/**
 *
 * @author lachlan
 */
public class DependencyAnalyserMojoTest extends AbstractMojoTestCase {

    //public void testExecute() throws Exception {
    //}
    public void testExecute() throws Exception {
        File pom = getTestFile("src/test/resources/unit/lct/pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());

        DependencyAnalyserMojo mojo = (DependencyAnalyserMojo) lookupMojo("dep-analyser", pom);

        //assertNotNull(mojo);
        //assertNotNull(mojo.project);

        //mojo.execute();
    }
}