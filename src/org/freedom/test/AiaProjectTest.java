package org.freedom.test;

import org.freedom.cluster.AiaProject;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by chaolin on 2017/4/20.
 */
public class AiaProjectTest {
    public static void main(String[] args) throws FileNotFoundException {
        File file=new File("D:\\test\\standard\\a4353.aia");
        AiaProject aia=new AiaProject(file);
        System.out.println("AiaProject.isValid: "+aia.isValid());
        System.out.println("mutations: "+aia.getMutationCounter());
    }
}
