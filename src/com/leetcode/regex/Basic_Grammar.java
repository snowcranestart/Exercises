package com.leetcode.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LYuan on 2016/10/19.
 * Intro-level Exercises for Regex.
 */
public class Basic_Grammar {
    public static void main(String[] args) {
        quantifier_Test();
//        branching_Test();
//        grouping_Test();
//        escapeCharacter_Test();
//        contentMatcher_Test();
//        boundaryMatcher_Test();
//        characterMatcher_Test();
//        negationMatcher_Test();
    }

    /**
     *  目录：
     *  1. 数量限定: Quantifier
     *  2. 分支: Branching
     *  3. 分组: Grouping
     *  4. 转义: Escape
     *  5. 内容限定: Content-Matcher
     *  6. 位置限定: Boundary-Matcher
     *  7. 字符限定: Character-Matcher
     *  8. 反义: Negation
     *
     */

    /** Quantifier: 数量限定符，限定前面出现的单独这个字符<允许出现的次数>。只有出现次数在规定范围内才算匹配。*/
    static void quantifier_Test() {
        /** Greedy 贪婪模式（默认）：<尽可能多>的匹配 */
        // 不加: 出现次数 == 1 (1)  如果字符后面没有跟数量限定符，那么就说明这个字符"有且仅能"出现1次。
        // *: 出现次数 >= 0 (0, 1, n)
        // +: 出现次数 > 0 (1, n)
        // ?: 出现次数 < 2 (0, 1)
        // {n}: 出现次数 == n
        // {n,}: 出现次数 >= n
        // {n,m}: 出现次数 n <= x <= m （特别注意：这里的大括号中不能有空格。{2,3}是正确的，{2, 3}将会报错。）
        find("Fuck", "FuckFucker");             // 每个字符都要出现，且只能出现一次，且必须按给定顺序出现。
        find("Fu*ck", "Fck Fuck Fuuuuck");      // u可以出现：0次，1次，n次
        find("Fu+ck", "Fck Fuck Fuuuuck");      // u可以出现：1次，n次
        find("Fu?ck", "Fck Fuck Fuuuuck");      // u可以出现：0次，1次
        find("Fu{3}ck", "Fck Fuck Fuuuck");     // u可以出现：3次
        find("Fu{2,}ck", "Fuck Fuuck Fuuuck");  // u可以出现：大于等于2次
        find("Fu{2,3}ck", "Fuck Fuuck Fuuuck"); // u可以出现：2次，3次
        // 注意，贪婪模式下，会试图一上来匹配的时候就吃下整个字符串，不匹配的话则会不断从右侧缩小字符串长度，直到匹配成功，或无法再缩小。
        // 特别的，每次匹配成功之后，如果当前匹配的字符串只是整体字符串的一部分，则会将剩下未匹配的字符串作为新的吃下去的字符串，直至未匹配区域为0.
        // 例如正则表达式"[af]+"匹配字符串"afaf0a0ff"。
        // 将会首先吃下"afaf0a0ff"，直到收缩至"afaf"。匹配成功一次。
        // 然后接着吃下"0a0ff"，收缩至""。匹配失败。
        // 接着吃下"a0ff"，收缩至"a"。匹配成功一次。
        // 接着吃下"0ff"，收缩至""。匹配失败。
        // 接着吃下"ff"，直接匹配成功。
        // 因此以下表达式的返回结果是："afaf", "a", "ff"
        find("[af]+", "afaf0a0ff");

        /** Reluctant(Lazy) 懒惰模式（在*,+,?,{}后添加<?>号）：<尽可能少>的匹配 */
        // 注意正则表达式扫描matcher字符串只会扫描一次，在扫描的过程中判断是否匹配，已经扫描且匹配的区间不会成为一个更大的匹配结果的一部分。
        // 例如用正则表达式a.*?扫描"abaab"，第一个ab符合最小匹配，接着第二个aab也符合匹配，此时已经扫描完毕。
        // 虽然整体abaab也符合匹配要求，但是由于不会重复扫描，因此不会讲abaab也算作匹配结果。
        // *?: >= 0，尽可能少的匹配
        // +?: > 0，尽可能少的匹配
        // ??: 0, 1，尽可能少的匹配
        // {n,}?: >= n，尽可能少的匹配
        // {n, m}?: m>=x>=n，尽可能少的匹配
        find("a.*b", "abaab");          // 贪婪，在扫描过程中寻找到最大匹配，因此匹配结果是：abaab at (0, 5)
        find("a.*?b", "abaab");         // 懒惰，从字符串开始扫描，只要达到匹配最低标准就算匹配，因此匹配结果有两个：ab 和 aab (没有abaab)
        find("F*", "AFF");              // 贪婪，因此只会返回一个匹配结果：AFF at (0, 4)
        find("F*?", "AFF");             // 懒惰，因为*?最少可以匹配0次，因此匹配结果是：5个空字符串"". 位置分别在(0,0)(1,1)(2,2)(3,3)
        find("(Fuck)+", "FuckFuck");    // 贪婪，因此只会返回一个匹配结果：FuckFucker at (0, 8)
        find("(Fuck)+?", "FuckFuck");   // 懒惰，只要找到一个满足的就返回，因此有两个匹配结果：Fuck at (0,4) (4, 8) (没有FuckFuck)
    }

    /** Branching：分支，使用|号。经常结合Grouping使用 */
    // 分支条件是从左向右顺序判定的，只要遇到一个分支匹配成功就算匹配了一次，且此时这个分支后面的分支将不会执行，并重新开始从第一个分支开始判定。
    static void branching_Test() {
        // 用"|"号结合小括号，等效于[ui]，详见字符限定章节。
        find("F(u|i)ck", "Fack Fuck Fick");             // 等效于或运算。

        /** 分支条件的顺序特性：难匹配的分支放前面，易匹配的放后面。*/
        find("\\d|\\d{2}", "0 99");         // 匹配结果并不是"0","99"，而是"0","9","9"。实际上第二个分支永远不会被匹配。
        find("\\d{2}|\\d", "0 99");         // 调换分支顺序后才可以匹配到"0","99". 难匹配的分支应该放在前面，否则难的分支永远轮不上执行。
        find("\\d{1,2}", "0 99");           // 或者直接用{n,m}，默认贪婪模式，同样可以匹配到"0","99".

        // 用"|"分隔多种可能的分支。
        // 下面的表达式用来匹配：区号可以用括号括起来，也可以不用括号括起来。连字符可以用减号，也可以用空格，也可以没有间隔。
        // 但是可以看到没有考虑全的情况：括号左右半边可能不同时出现。
        find("\\(?0\\d{2}\\)?[- ]?\\d{8}|0\\d{2}[- ]?\\d{8}", "010)-12345678");     // 匹配到了不想匹配的情况。
        find("\\(0\\d{2}\\)[- ]?\\d{8}|0\\d{2}[- ]?\\d{8}", "010)-12345678");       // 不匹配。
        find("(\\(0\\d{2}\\)|0\\d{2})[- ]?\\d{8}", "(010)-12345678");               // 使用小括号分组，缩短表达式。
    }

    /** Grouping：分组，使用小括号()。经常结合Branching使用，定义操作符的范围和优先顺序 */
    static void grouping_Test() {
        // 小括号决定了其他符号的作用范围
        find("gr(a|e)y", "grey gray");      // |分支运算被限制于括号内。返回匹配结果为"grey", "gray".
        find("gra|ey", "gray");             // 相比之下，如果没有使用小括号，则变成了匹配gra或ey的表达式了。返回匹配结果为"gra"
        find("(Shit)*X", "ShitShitX");      // *号作用于小括号内的整体，"Shit"被看作一个字符对待。

        // 组号：用斜杠"\"搭配数字。每个小括号都是一个分组，并自左向右依次获得一个组号，从1开始计数。
        // "\组号"就表示第一个分组所匹配得到的内容。
        // 例如下面的表达式中，\1指的是组号1的匹配结果，这里是a，\2指的是组号2的匹配结果，这里是c
        // 所以表达式等效于匹配两个重复的单词。
        find("(a|b)(c|d)\\1\\2", "acac");       // 匹配结果为"acac".

        /** 综合练习：提取字符串中的所有合法IPv4地址（注：IP地址前面补0是合法的） */

        /** Try 1 */
        // 小括号括起来的"\\d{1,3}\\."表示从0到999的数。重复三次，精简了表达式长度。
        // 但取值范围过大，不满足每个区段只能为0-255的限制。
        find("(\\d{1,3}\\.){3}\\d{1,3}", "192.168.0.1 10.61.0.17");

        /** Try 2 */
        // 为了做到正确区段的IP匹配，首先需要解决匹配0-255的问题，将这个问题分解为3类情况：0-199，200-249，250-255.
        // case 1：0-199：[01]?\d?\d (X, XX, 0XX, 1XX)（前两位都可以没有，或只出现一次。对于百位出现但十位没出现的情况，依然正常。）
        // case 2：200-249：2[0-4]\d (20X, 21X, 22X, 23X, 24X)
        // case 3：250-255：25[0-5] (250, 251, 252, 253, 254, 255)
        // 但是匹配结果却并不准确，把200,255这些合法的值都截断匹配成了20,0,25,5
        // 根本原因在于分支条件的顺序执行特性，只要匹配到一个分支就不会继续判定后面的分支了。
        // 这里由于case1的表达式写在最前面，因此只要匹配到case1，后面的case2和case3就不会匹配了。因此需要把顺序调换一下。
        find("[01]?\\d?\\d|2[0-4]\\d|25[0-5]", "0 9 09 19 99 001 019 099 123 199 200 255 256 348 999");

        /** Try 3 */
        // 调换顺序，case3-case2-case1。最难匹配的分支放在最前面，没匹配成还有后面分支的接着，类似贪婪模式。
        // 匹配结果是：0, 9, 09, 19, 99, 001, 019, 099, 123, 199, 200, 255, 25, 6, 34, 8, 99, 9
        // 可以看到，除了大于255的数值被截断匹配，其他小于等于255的值都被完整的正确匹配了。
        find("25[0-5]|2[0-4]\\d|[01]?\\d?\\d", "0 9 09 19 99 001 019 099 123 199 200 255 256 348 999");

        /** Try 4 */
        // 基于上面的表达式匹配完整的四段IP地址
        // 可以看到能够正确忽略非法的"1.256.1.1"
        // 但是却把"725.192.1.1"部分匹配成"25.192.1.1"，把"192.168.0.256"部分匹配成"192.168.0.25"，将原本非法的IP截断了。
        // 这不是我们想要的，因为他创造了一个并不存在的IP地址。这就需要我们主动限定非空字符的起始和结束位置了。
        find("((2[0-5]{2}|[0-1]\\d{2}|\\d{1,2})\\.){3}(2[0-5]{2}|[0-1]\\d{2}|\\d{1,2})",
                "1.256.1.1 725.192.1.1 99.1.1.1 192.168.0.256");

        /** Try 5: Finally! Correct Solution! */
        // 增加位置限定符，避免部分匹配非法IP。下面的表达式可以正确的只匹配出一个结果："99.1.1.1" 另外两个不会被部分匹配到。
        find("\\b((2[0-5]{2}|[0-1]\\d{2}|\\d{1,2})\\.){3}(2[0-5]{2}|[0-1]\\d{2}|\\d{1,2})\\b",
                "1.256.1.1 725.192.1.1 99.1.1.1 192.168.0.256");
    }

    /** Escape：使用斜杠 "\"
     *  想在Java字符串中使用<正则表达式规定的转义字符>，必须用<双斜杠>，因为<单斜杠>只能用在Java语言自己的转义字符上。 */
    static void escapeCharacter_Test() {
        // 要明确，“Java语言的转义字符”和“正则表达式的转义字符”是两个完全独立的东西，两套体系，不是一码事。
        // 只有Java语言中规定的转义字符，才能够用<单斜杠>，例如\n, \t, \r.
        // 正则表达式所规定的转义字符，例如\w, \d, \s，在Java中并没有, 所以用单斜杠Java是不认的，需要用双斜杠，即写成\\w, \\d, \\s才可以识别。
        // 所以虽然下面介绍的正则表达式都是单斜杠，但是实际用在代码中，还是要写成双斜杠的放在字符串中才可以工作。

        /** 匹配特殊符号本身 */
        // 正则表达式中，对于本身具有特殊含义的符号，如果需要匹配原符号本身，则需要在前面加转义字符"\".
        // 例如：\*, \+, \?, \{, \},\(, \), \[, \], \-, \., \^, \$,
        //     ( 出 现 次 数 限 定 )  (分区) (字符集)         (位置限定)
        find("\\*\\+\\?\\{\\}\\(\\)\\[\\]\\-\\.\\^\\$", "*+?{}()[]-.^$");       // 成功匹配整个字符串。
    }

    /** Content Matcher: <匹配特定类型内容>的元字符（简称内容限定符） */
    // Meta-Character：元字符，即正则表达式专用的转义字符，有的可以用来匹配特定字符内容，有的则可以用来匹配特定位置
    // 注意：区别java自己的转义字符（\n,\t...）和正则表达式专用的转义字符（\w,\b...）。后者放在字符串中必须再加一个转义符\，形如"\\w", "\\b".
    static void contentMatcher_Test() {
        // . : 代表任意字符，除了换行符"\n"
        // \w: 代表任意非空白字符（字母、数字、下划线、汉字）
        // \s: 代表任意空白字符（空格，中文全角空格，换行"\n"，制表符"\t"）
        // \d: 代表任意数字。

        /**  . - 匹配：任意字符 */
        // .*表示任意长度的任意字符串，只要遇到换行符就算匹配结束。
        // 因此下面语句返回匹配结果如下：
        // => "abc" at (0, 3)
        // => "" at (3, 3)
        // => "12345 at (4, 9)
        // => "" at (9, 9)
        // => "" at (10, 10)
        // 为什么有这么多空字符串？让我们来分析一下。首先分解原字符串：
        //    0   1   2    3   4   5   6   7   8    9     (字符串索引）
        //    ↑   ↑   ↑    ↑   ↑   ↑   ↑   ↑   ↑    ↑
        //    a   b   c   \n   1   2   3   4   5   \n
        //  ↑   ↑   ↑   ↑    ↑   ↑   ↑   ↑   ↑   ↑    ↑
        //  0   1   2   3    4   5   6   7   8   9    10  (匹配位置索引)
        // 从左向右扫描，寻找最大匹配（即只有遇到换行符才结束匹配）
        // 匹配1: 0 - 3 = "abc"
        // 匹配2: 3 - 3 = ""
        // 匹配3: 4 - 9 = "12345"
        // 匹配4: 9 - 9 = ""
        // 匹配5: 10 - 10 = "" (容易忽视，最后一个字符结尾处也算一个索引。)
        find(".*", "abc\n12345\n");         // 匹配到5个结果，如上面分析所示。
        find(".", "a");                     // 单独的一个"."只匹配一个任意非换行字符。这里匹配"a".
        find(".", "");                      // 和*不一样，"."只匹配长度为1的任意非换行字符。这里没有匹配结果。

        /**  \w - 匹配：任意非空白字符 (w = word) */
        // \\w\\w\\w表示三个非空白字符相连。
        // 由于先发现的是"abc 123"，已经匹配，这里的"123"已经使用（扫描过），不会重复与"456"再组合匹配。
        find("\\w\\w\\w \\w\\w\\w", "abc 123 456");

        /**  \s - 匹配：任意空白字符 (s = space) */
        // \\s{2,}表示连续出现大于等于2两个空白字符。因此可以匹配到两个结果："Mi   el" at (0,7)，以及"Mi \t el" at (14, 21)
        find("Mi\\s{2,}el", "Mi   el Mi el Mi \t el");

        /**  \d - 匹配：任意数字字符 (d = digit) */
        find("\\d{4,}", "123443215678");    // 贪婪：匹配最长的，即只有一个匹配结果"123443215678"
        find("\\d{4,}?", "123443215678");   // 懒惰：匹配最短的，扫描过程中有四个结果："1234", "4321", "5678".
        find("\\d{4,}", "123456 12345");    // 贪婪：要明白，贪婪匹配的前提，是首先能够符合匹配条件。
                                            // 因此扫描至空格时第一个匹配就结束了，这也是当前能匹配的最长字符串了。从下一个元素开始新的匹配。
                                            // 最后的匹配结果有两个："123456" at (0, 6), "12345" at (7, 12).
        find("\\d{4,}?", "123456 12345");   // 懒惰：匹配的结果也是两个："1234" at (0, 4), "1234" at (7, 11).
    }

    /** Boundary Matcher：<匹配特定位置>的元字符（简称位置限定符）。这类元字符并不匹配具体内容，而是限定了具体位置。 */
    static void boundaryMatcher_Test() {
        // \b: 匹配任意单词的开始和结束位置
        // ^: 匹配整个字符串的开始位置
        // $: 匹配整个字符串的结束位置

        /**  \b - 匹配任意单词的开始和结尾位置 (b = boundary) */
        // 被\\b包围的单词，意味着只匹配这个单词本身，而不匹配那些包含这个单词内容的单词。
        // 下面的字符串中，只有"fuck"才能被匹配到。
        // "fucker"这个单词的结尾并不是fuck，所以不匹配
        // "Ufuck"这个单词的开头并不是fuck，所以不匹配
        // "fuckfuck"这个单词开头和结尾中间的内容不是一个fuck而是两个，所以不匹配。
        find("\\bfuck\\b", "fuck fucker Ufuck fuckfuck");
        // 下面的regex中由于只要求fuck必须出现在一个单词的开头，因此可以匹配到两次fuck，分别位于(0, 4) and (5, 9).
        find("\\bfuck", "fuck fucker Ufuck");

        /** ^ - 匹配整个字符串的起始位置(0) */
        /** $ - 匹配整个字符串的结束位置(length) */
        // 有的时候，你想匹配的是matcher对应的整个字符串，而不关系用从这个字符串中匹配出多少次来。这时候用^和$来限定位置即可。
        find("^1.*", "21234");      // 表示只匹配以字符"1"开头的任意长度字符串。虽然"21234"包含字符1，但是不是以字符1开头，因此匹配不到任何结果。
        find("^1.*", "12345");      // 因为整个字符串的确以1开头，因此可以成功贪婪匹配到整个字符串"12345".
        find("^1.*X$", "12345");    // 表示只匹配以"1"开头且以"X"结尾的任意长度字符串。虽然"12345"以1开头，但是没有以X结尾，因此匹配不到任何结果。
        find("^1.*X$", "1234X");    // 成功匹配整个字符串"1234X".
        find("^1.*X$", "asd1234Xff1Xj91,,X");   // 匹配整个字符串，匹配不到任何结果。
        find("1.*X", "asd1234Xff1Xj91,,X");     // 相比之下，如果没有使用位置限定符，单靠"1.*X"匹配，只要字符串包含这类元素就可以匹配成功
        //               ↑             ↑        // 这里贪婪匹配可以得到一个结果："1234Xff1Xj91,,X" at (3, 18).
        find("1.*?X","asd1234Xff1Xj91,,X");     // 同样的，使用懒惰模式可以匹配到三个结果："1234X", "1X", "1,,X"
    }

    /** Character Matcher：使用中括号[]和减号-，匹配属于给定字符集的字符。比上面的内容限定符（\w, \s, \d）更灵活。 */
    static void characterMatcher_Test() {
        /** [ ]: 中括号被看作一个整体，只匹配一个字符。中括号中列举了所匹配的这个字符的取值范围。*/
        find("[fuck]", "abcdefg");          // 只匹配任意一个字符，取值范围为"f", "u", "c", "k". 2个匹配结果："c", "f".
        find("[aeiou]", "cake");            // 只匹配原音字母。2个匹配结果："a", "e".

        /** -: 减号用来表示一个取值区间，省去全部列举的麻烦。*/
        find("[0-9]", "987");               // 等效于\d. 只匹配字符0到9。3个匹配结果："9", "8", "7".
        find("[0-9A-Za-z_]", "a0A@#_");     // 类似于\w. 之匹配字符、数字和下划线。匹配结果："a", "0", "A", "_".
    }

    /** 反义：匹配<不满足特定条件>的字符 */
    static void negationMatcher_Test() {
        /** [^...]: "^"号搭配中括号使用，依然表示匹配一个字符，但表示除了中括号中字符都匹配。*/
        find("[^aeiou]", "cake");           // 匹配除了元音字母之外的任何字符。匹配结果："c", "k".
        find("[aeiou]", "cake");            // 只匹配原音字母。2个匹配结果："a", "e".

        /** 大写内容匹配符：\W, \D, \S */
        // \W: 匹配除字母数字下划线汉字之外的任意一个字符。
        // \D: 匹配除数字之外的任意一个字符。
        // \S: 匹配除空白字符之外的任意一个字符。
        find("\\W\\W", " af92\t\ta91_");    // 匹配除了单词之外的字符，匹配结果："\t\t".
        find("\\w\\w", " af92\t\ta91_");    // 匹配单词，匹配结果："af92", "a91_".

        /** [^...] 搭配字符集限定符使用 */
        find("[a-z&&[^hov]]", "abhiodvz");  // 即使用了字符集限定，又同时给出了需要刨除的例外情况。之匹配a到z中除h/o/v之外的任意字符。
    }

    static void stringMatchesAPI_Test() {
        // Or
        print("F(u|i)ck", "Fack");

        // Quantifier
        print("Fuck", "Fuck");
        print("Fu*ck", "Fuuuuuuck");
        print("Fu+ck", "Fck");
        print("Fu?ck", "Fuuck");
        print("Fu{3}ck", "Fuuuck");
        print("Fu{2,}ck", "Fuuuck");
        print("Fu{2,4}ck", "Fuuuck");

        // Grouping
        print("(Shit)*X", "ShitShitX");

        // Metacharacters
        print(".*", "a;lsdkfj");                            // .*表示匹配任意长度的任意字符串，只要这个字符串内没有换行符就匹配。
        print("\\w \\w \\w", "1 c  ");                      // 表示专门匹配三个非空白符的字符，中间用空格相连。例如"1 2 3"
        print("\\s*Shit", "    \t   Shit");                 // \s*表示任意多个空白符。
        print("Mobile: \\d{11}", "Mobile: 13901234567");    // \d{11}表示匹配十一位连续出现的数字，即手机号。
        print("\\bFuck\\b", "Fuckish");                     // 只匹配Fuck这个单词，因此无匹配。
    }

    /** 使用 String.matches(String regex): 判断<整个字符串>是否匹配正则表达式 */
    // 要特别注意，Java提供的String类库的matches()方法返回true的条件是<整个字符串>都必须匹配
    // 也就是说，不管你定义的正则表达式regex是什么内容，这个API实际处理的都是^regex$，即强制从字符串的开头开始匹配，并要一直匹配到结尾。
    private static void print(String regex, String str) {
        boolean match = str.matches(regex);
        System.out.println("[" + regex + "] -> " + "\"" + str + "\"" + " -> " + match);
    }

    /** 使用 java.util.regex 类库：可以判断正则表达式是否能够匹配所给字符串的局部或全部，如果可以匹配，则给出所有匹配的位置区间 */
    // regex类库是专门进行正则匹配的库，功能很全面和强大。
    // 需要使用Pattern的静态工厂方法构造正则表达式的匹配器(pattern)，
    // 然后再由待匹配字符串定义一个matcher对象，使用find()来搜索。
    // 最后用start() / end()显示匹配的起始位置和终止位置，
    // 要注意的是，这里的位置索引与数组的索引不同，如下图所示：
    // 数组索引：    [____0____] [____1____] [____2____] [____3____] [____4____]
    // 匹配索引：   <0>        <1>         <2>         <3>         <4>        <5>
    // 所以即使字符串索引只有0-4，匹配索引取值范围也可以达到0-5.
    private static void find(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        boolean find = false;
        System.out.println("---------------------------------------------------------------------------");
        System.out.println("@ Regex: [" + regex + "] | @ Matcher: [" + str + "]");
        while (matcher.find()) {
            find = true;
            System.out.println("=> Found \"" + matcher.group() + "\" at (" + matcher.start() + ", " + matcher.end() + ").");
        }
        if (!find) System.out.println("=> No Match Found.");
    }
}
