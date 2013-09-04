/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.lachlanap.dependencyanalyser;

import java.net.URL;
import java.net.URLClassLoader;
import org.apache.bcel.util.ClassLoaderRepository;
import org.apache.bcel.util.Repository;

/**
 *
 * @author lachlan
 */
public class RepositoryFactory {

    public static Repository getRepo(URL jar) {
        URLClassLoader loader = new URLClassLoader(new URL[]{jar}, RepositoryFactory.class.getClassLoader());
        return new ClassLoaderRepository(loader);
    }
}
