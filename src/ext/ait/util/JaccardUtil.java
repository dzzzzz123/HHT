package ext.ait.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 这个帮助类旨在使用Jaccard算法比较来获得两个字符串集的相似度
 */
public class JaccardUtil {
    private static JaccardUtil instance;
    // 需要计算相似性的数据集
    private List<String> list;
    // 需要计算相似性的数据集的set集合
    private Set<String> set;
    // 被用来一个个计算相似性的数据集
    private Map<String, List<String>> listToCompute;
    // 结果集，输出所有数据所对应的相似性结果集
    private Map<String, String> result;

    private JaccardUtil(List<String> list, Map<String, List<String>> listToCompute) {
        this.list = list;
        this.listToCompute = listToCompute;
        this.set = new HashSet<>(this.list);
    }

    public static JaccardUtil newJaccardUtil(List<String> list, Map<String, List<String>> listToCompute) {
        if (instance == null) {
            instance = new JaccardUtil(list, listToCompute);
        }
        return instance;
    }

    public void processAllRow() {
        result = new HashMap<>();
        listToCompute.forEach((key, value) -> {
            System.out.println("key:" + key);
            double doubleSimilarity = processOneRow(value);
            System.out.println("output:" + doubleSimilarity);
            String strSimilarity = String.format("%.2f", doubleSimilarity);
            result.put(key, String.valueOf(strSimilarity));
        });
        result.forEach((key, value) -> {
            System.out.println("key:" + key + "; value:" + value);
        });
    }

    public double processOneRow(List<String> list2) {
        Set<String> set2 = new HashSet<>(list2);

        Set<String> union = new HashSet<String>();
        union.addAll(set);
        union.addAll(set2);

        int inter = set.size() + set2.size() - union.size();
        return 1.0 * inter / union.size();
    }

    // Test
    public static void main(String[] args) {
        List<String> list = Arrays.asList("1", "1", "1", "1", "1");
        Map<String, List<String>> map = Map.of("1", Arrays.asList("1", "1", "2", "4", "5"), "2",
                Arrays.asList("1", "2", "1", "4", "5"), "3", Arrays.asList("1", "0", "1", "1", "1"), "4",
                Arrays.asList("1", "2", "1", "1", "5"), "5", Arrays.asList("1", "2", "3", "4", "5"));
        JaccardUtil jaccardUtil = JaccardUtil.newJaccardUtil(list, map);
        jaccardUtil.processAllRow();
    }
}
