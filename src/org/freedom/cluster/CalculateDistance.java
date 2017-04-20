package org.freedom.cluster;

import java.util.*;

/**
 * Created by chaolin on 2017/4/20.
 */
public class CalculateDistance {
    // 距离求解的项目集合
    private List<AiaProject> aiaProjects;
    // 全部项目中mutation的类型和包含该mutation的项目数
    private Map<String, Integer> mutationCountProject = new HashMap<>();
    // 全部项目中每种mutation的权重
    private Map<String, Double> mutationIDF = new HashMap<>();
    // 项目的最大距离
    public static final double MAX_DISTANCE = 100.0;

    public CalculateDistance(List<AiaProject> aiaProjects) {
        this.aiaProjects = aiaProjects;
        // TF-IDF
        int totalProjects = aiaProjects.size();
        for (AiaProject aiaProject : aiaProjects) {
            if (aiaProject == null || !aiaProject.isValid()) {
                continue;
            }
            Map<String, Integer> counter = aiaProject.getMutationCounter();
            for (String mutation : counter.keySet()) {
                Integer resultCount = mutationCountProject.get(mutation);
                if (resultCount == null) {
                    resultCount = 1;
                } else {
                    resultCount++;
                }
                mutationCountProject.put(mutation, resultCount);
            }
        }

        // calculate IDF
        for (String mutation : mutationCountProject.keySet()) {
            double idf = Math.log10(totalProjects * 1.0 / mutationCountProject.get(mutation)+1);
            mutationIDF.put(mutation, idf);
        }

        // generate eigenvector
        for (AiaProject aiaProject : aiaProjects) {
            if (aiaProject == null || !aiaProject.isValid()) {
                continue;
            }
            int totalMutation = 0;
            Collection<Integer> counters = aiaProject.getMutationCounter().values();
            for (int count : counters) {
                totalMutation += count;
            }
            Set<String> mutations = aiaProject.getMutationCounter().keySet();
            for (String mutation : mutations) {
                // TF*IDF
                aiaProject.getVector().put(mutation, (aiaProject.getMutationCounter().get(mutation) * 1.0
                        * mutationIDF.get(mutation) / totalMutation));
            }
        }
    }

    public double calculateDistance(AiaProject aiaProjectA, AiaProject aiaProjectB) {
        // consine theorem solving similarity
        if (aiaProjectA == null || aiaProjectB == null || !aiaProjectA.isValid() || !aiaProjectB.isValid()) {
            return -1;
        }
        Set<String> mutations = new HashSet<>();
        mutations.addAll(aiaProjectA.getMutationCounter().keySet());
        mutations.addAll(aiaProjectB.getMutationCounter().keySet());
        double member = 0;
        for (String mutation : mutations) {
            double valueA = getMapValue(aiaProjectA.getVector(), mutation, 0.0);
            double valueB = getMapValue(aiaProjectB.getVector(), mutation, 0.0);
            member += valueA * valueB;
        }

        double denominator;
        double lengthA = 0;
        double lengthB = 0;
        for (String mutation : aiaProjectA.getMutationCounter().keySet()) {
            lengthA += Math.pow(aiaProjectA.getVector().get(mutation), 2.0);
        }
        lengthA = Math.sqrt(lengthA);
        for (String mutation : aiaProjectB.getMutationCounter().keySet()) {
            lengthB += Math.pow(aiaProjectB.getVector().get(mutation), 2.0);
        }
        lengthB = Math.sqrt(lengthB);
        denominator = lengthA * lengthB + 0.0000001;
        double cos = member / denominator;

        // convert to distance
        return 100.0 - (cos * 100.0);
    }

    private <K, V> V getMapValue(Map<K, V> map, K key, V defaultValue) {
        V result = map.get(key);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    public double[][] calculateAllDistance() {
        double[][] disMatrix = new double[aiaProjects.size()][aiaProjects.size()];
        for (int i = 0; i < aiaProjects.size(); i++) {
            for (int j = i; j < aiaProjects.size(); j++) {
                if (j == i) {
                    disMatrix[i][i] = 0.0;
                } else {
                    disMatrix[i][j] = disMatrix[j][i] = calculateDistance(aiaProjects.get(i), aiaProjects.get(j));
                }
            }
        }
        return disMatrix;
    }

    public Map<AiaProject, Map<AiaProject, Double>> calculateAllDistanceToMap() {
        Map<AiaProject, Map<AiaProject, Double>> disMap = new HashMap<>();
        for (int i = 0; i < aiaProjects.size(); i++) {
            Map<AiaProject, Double> tmp = new HashMap<>();
            for (int j = i + 1; j < aiaProjects.size(); j++) {
                tmp.put(aiaProjects.get(j), calculateDistance(aiaProjects.get(i), aiaProjects.get(j)));
            }
            disMap.put(aiaProjects.get(i), tmp);
        }
        return disMap;
    }

    public List<AiaProject> getAiaProjects() {
        return aiaProjects;
    }

    public void setAiaProjects(List<AiaProject> aiaProjects) {
        this.aiaProjects = aiaProjects;
    }

    public Map<String, Integer> getMutationCountProject() {
        return mutationCountProject;
    }

    public void setMutationCountProject(Map<String, Integer> mutationCountProject) {
        this.mutationCountProject = mutationCountProject;
    }

    public Map<String, Double> getMutationIDF() {
        return mutationIDF;
    }

    public void setMutationIDF(Map<String, Double> mutationIDF) {
        this.mutationIDF = mutationIDF;
    }
}
