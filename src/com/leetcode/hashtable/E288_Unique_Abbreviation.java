package com.leetcode.hashtable;

import java.util.*;

/**
 * Created by LYuan on 2016/9/2.
 * An abbreviation of a word follows the form <first letter><number><last letter>.
 * Below are some examples of word abbreviations:
 * a) it                      --> it    (no abbreviation)
 * b) d|o|g                   --> d1g
 * c) i|nternationalizatio|n  --> i18n
 * d) l|ocalizatio|n          --> l10n
 * Assume you have a dictionary and given a word,
 * find whether its abbreviation is unique in the dictionary.
 * A word's abbreviation is unique if no other word from the dictionary has the same abbreviation.
 *
 * Example:
 * Given dictionary = [ "deer", "door", "cake", "card" ]
 * isUnique("dear") -> false
 * isUnique("cart") -> true
 * isUnique("cane") -> false
 * isUnique("make") -> true
 *
 * Your ValidWordAbbr object will be instantiated and called as such:
 * ValidWordAbbr vwa = new ValidWordAbbr(dictionary);
 * vwa.isUnique("Word");
 * vwa.isUnique("anotherWord");
 *
 * Function Signature:
 * public ValidWordAbbr(String[] dictionary) {...}
 * public boolean isUnique(String word) {...}
 *
 * <Tags>
 * - HashSet: 快速去重。导入ArrayList或Arrays.asList()。
 * - HashMap: Key → 字符串模板（缩写），Value → 标志是否Unique
 *
 */
public class E288_Unique_Abbreviation {
    public static void main(String[] args) {
        ValidWordAbbr2 dict2 = new ValidWordAbbr2(new String[]{"deer", "door", "cake", "card"});
        System.out.println(dict2.isUnique("dear")); // false
        System.out.println(dict2.isUnique("cart")); // true
        System.out.println(dict2.isUnique("cane")); // false
        System.out.println(dict2.isUnique("make")); // true
        ValidWordAbbr dict = new ValidWordAbbr(new String[]{"deer", "door", "cake", "card"});
        System.out.println(dict.isUnique("dear")); // false
        System.out.println(dict.isUnique("cart")); // true
        System.out.println(dict.isUnique("cane")); // false
        System.out.println(dict.isUnique("make")); // true
    }
}

/** 解法2：HashSet去重 + HashMap映射（键值对定义为String,Boolean）。更简洁的写法。 */
// 可以看到代码简洁程度和清晰程度有大幅度提升，说明这几个月的算法训练是有效果的。
class ValidWordAbbr2 {
    Map<String, Boolean> map = new HashMap<>();
    Set<String> set;
    public ValidWordAbbr2(String[] dict) {
        set = new HashSet<>(Arrays.asList(dict));       // 首先导入到HashSet中去重，以避免对["a", "a"]这种情况误判成false。
        for (String s : set) {
            String pattern = getAbbr(s);
            if (map.containsKey(pattern)) map.put(pattern, false);  // 此时dict中没有完全相同的字符串，因此只要遇到相同的pattern，就需要置为false
            else map.put(pattern, true);                            // 如果map中尚未存储该pattern，则首先置为true
        }
    }

    /** 可能分支较多，脑子要清楚，需要分成五种情况讨论，区分word和dict自身的独特性。 */
    // Case #1: word in dict, pattern in map is false -> false
    // Case #2: word in dict, pattern in map is true -> true
    // Case #3: word not in dict, pattern in map, pattern is true -> false
    // Case #4: word not in dict, pattern in map, pattern is false -> false
    // Case #5: word not in dict, pattern not in map -> true
    public boolean isUnique(String word) {
        String p = getAbbr(word);
        if (set.contains(word)) return map.get(p);
        else return !map.containsKey(p);
    }

    private String getAbbr(String s) {
        if (s.length() < 3) return s;
        return "" + s.charAt(0) + String.valueOf(s.length() - 2) + s.charAt(s.length() - 1);    // 首先加上空字符串是为了避免char自动转型为int计算
    }
}

/** 解法1：老解法。HashMap的键值对定义是<String,String>. */
// 哈希表解法，难点在于如何处理dictionary中出现多个不同的字符串具有相同pattern时如何返回false
// 题目没有说清楚什么时候给的word叫unique：有以下几种情况
// 1. word的pattern根本dictionary中就没有，即：!map.containsKey(pattern)
// 2. word的pattern在dictionary中有，但是dictionary中所有pattern和word一样的字符串都和word拼写一模一样。即：word.equals(map.get(pattern))
// 将每个字符串的pattern作为Key，原始字符串作为Value，
// 在扫描入库dictionary的时候就统计是否有不同Value对应一个Key的情况出现，
// 如果有的话，就把这个Value设为一个不可能与任何word一样的值，避免被匹配到
class ValidWordAbbr {
    private Map<String, String> map;
    public ValidWordAbbr(String[] dictionary) {
        map = new HashMap<>();
        for (String s : dictionary) {
            String pattern = getAbbr(s);
            if (!map.containsKey(pattern))
                map.put(pattern, s);
            else if (!s.equals(map.get(pattern)))
                map.put(pattern, "");
        }
    }

    public boolean isUnique(String word) {
        String pattern = getAbbr(word);
        if (!map.containsKey(pattern) || word.equals(map.get(pattern))) return true;
        else return false;
    }

    private String getAbbr(String s) {
        int l = s.length();
        if (l < 3)  return s;
        else        return String.valueOf(s.charAt(0)) + (l - 2) + String.valueOf(s.charAt(l - 1));
    }
}
