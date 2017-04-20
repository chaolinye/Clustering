package org.freedom.cluster;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by chaolin on 2017/4/20.
 * Aia项目类
 */
public class AiaProject {
    // 文件名
    private String name;
    // 标题
    private String title;
    // mutation的类型和个数
    private Map<String, Integer> mutationCounter = new HashMap<>();
    // 特征向量
    private Map<String, Double> vector = new HashMap<>();
    // 解析的项目是否有效
    private boolean isValid=true;

    public AiaProject(File file) {
        if (file.isDirectory() || !file.getName().toLowerCase().endsWith(".aia")) {
            throw new RuntimeException("can't parse a non-aia file: " + file.getAbsolutePath());
        }
        this.name = file.getName();
        try {
            parse(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.err.println(mutationCounter);
            isValid=false;
        }
    }

    public AiaProject(String name, InputStream file) {
        this.name = name;
        parse(file);
    }

    /**
     * 解析aia文件
     * @param file
     */
    private void parse(InputStream file) {
        System.out.println("start parse "+name);
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
                } else if (ze.getName().toLowerCase().endsWith(".bky")) {
                    parseBky(new ByteArrayInputStream(bo.toByteArray()));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(name + " is not a legal aia file.", e);
        } finally {
            try {
                zin.closeEntry();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                throw new RuntimeException(name+" close error",e);
            }
        }
        System.out.println("end parse "+name);
    }

    /**
     * 解析bky文件
     * @param bkyFile
     */
    private void parseBky(InputStream bkyFile) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            // The following procedure is synchronized.
            saxParser.parse(bkyFile, new BkySAXHandler(this));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(name + " parse bky error.", e);
        }
    }

    /**
     * 添加mutation
     * @param mutation
     */
    public void addMutation(String mutation) {
        Integer count = mutationCounter.get(mutation);
        if (count == null) {
            count = 0;
        }
        count++;
        mutationCounter.put(mutation, count);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Integer> getMutationCounter() {
        return mutationCounter;
    }

    public void setMutationCounter(Map<String, Integer> mutationCounter) {
        this.mutationCounter = mutationCounter;
    }

    public Map<String, Double> getVector() {
        return vector;
    }

    public void setVector(Map<String, Double> vector) {
        this.vector = vector;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

}
