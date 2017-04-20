package org.freedom.util;

import org.freedom.cluster.AiaProject;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by chaolin on 2017/4/20.
 */
public class FileUtil {
    /**
     * 查找路径下的Aia文件
     * @param path
     * @return
     */
    public static List<File> findAiaFile(File path){
        List<File> fileList=new ArrayList<>();
        if(path.isDirectory()){
            File[] files=path.listFiles();
            for(File file:files){
                fileList.addAll(findAiaFile(file));
            }
        }else{
            if(path.getName().toLowerCase().endsWith(".aia")){
                fileList.add(path);
            }
        }
        return fileList;
    }
    /**
     * 获取ZIP包中Aia项目集合
     * @param file
     * @return
     */
    public static List<AiaProject> UnzipToAiaProjects(InputStream file){
        List<AiaProject> aiaProjects=new ArrayList<>();
        InputStream in = new BufferedInputStream(file);
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry ze;
        try {
            while ((ze = zin.getNextEntry()) != null) {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int count = -1;
                byte[] data = new byte[1024];
                while ((count = zin.read(data)) != -1) {
                    bo.write(data, 0, count);
                }
                if (ze.isDirectory()) {
                } else if (ze.getName().toLowerCase().endsWith(".aia")) {
                    aiaProjects.add(new AiaProject(ze.getName(),new ByteArrayInputStream(bo.toByteArray())));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                zin.closeEntry();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();;
            }
        }
        return aiaProjects;
    }
}
