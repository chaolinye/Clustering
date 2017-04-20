package org.freedom.util;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.*;
/**
 * Created by chaolin on 2017/4/20.
 */
public class StringUtil {
    public static Set<String> expectedNature = new HashSet<String>();
    static {
        expectedNature.addAll(Arrays.asList("n", "v", "vd", "vn", "vf", "vx", "vi", "vl", "vg", "nt", "nz", "nw", "nl",
                "ng", "userDefine", "wh", "en"));
    }

    public static Map<String, Integer> segmentWord(String str) {
        List<Term> terms = ToAnalysis.parse(str).getTerms();
        if (terms.size() == 0)
            return null;
        Map<String, Integer> map = new HashMap<>();
        for (int k = 0; k < terms.size(); k++) {
            String word = terms.get(k).getName(); // 拿到词
            String natureStr = terms.get(k).getNatureStr(); // 拿到词性
            if (!expectedNature.contains(natureStr))
                continue;
            if (map.containsKey(word)) {
                map.put(word, map.get(word) + 1);
            } else {
                map.put(word, 1);
            }
        }
        return map;
    }

    public static void main(String[] args) {
        Map<String, Integer> map = segmentWord("hello world");
        if (map == null) {
            System.out.println("null");
        } else {
            System.out.println(map);
        }
    }
}
