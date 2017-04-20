package org.freedom.test;

import org.freedom.cluster.AiaProject;
import org.freedom.cluster.CalculateDistance;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaolin on 2017/4/20.
 */
public class CalculateDistanceTest {
    public static void main(String[] args) {
        AiaProject aia1=new AiaProject(new File("D:\\test\\standard\\a1113.aia"));
        AiaProject aia2=new AiaProject(new File("D:\\test\\standard\\a1124.aia"));
        List<AiaProject> list=new ArrayList<>();
        list.add(aia1);
        list.add(aia2);
        CalculateDistance c=new CalculateDistance(list);
        System.out.println("distance: "+c.calculateDistance(aia1, aia2));
    }
}
