从一道入群题说起：

https://bitbucket.org/snippets/centaur/XoyzE

在JDK中附带了一个工具[native2ascii](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/native2ascii.html)，用来将非ASCII字符转成ASCII字符串的形式，常用在[i18n](https://docs.oracle.com/javase/tutorial/i18n/)的场合。我们用scala来实现`native2ascii`和它的逆运算`ascii2native`

需要实现的方法签名：
```scala
def native2ascii(native: CharSequence): CharSequence
def ascii2native(ascii: CharSequence): CharSequence
```

测试用例：
```scala
assert(native2ascii("中abc").toString == "\\u4e2dabc")
assert(ascii2native("\\u4e2dabc").toString == "中abc")
assert(native2ascii("路·").toString == "\\u8def\\u00b7")
assert(ascii2native("\\u8def\\u00b7").toString == "路·")
assert(native2ascii("声明：此为贴吧助手插件的功能，不是百度贴吧原有功能～。设置称呼后，在帖子页面，您设置的称呼将会自动显示在自己的头像上。").toString == 
  "\\u58f0\\u660e\\uff1a\\u6b64\\u4e3a\\u8d34\\u5427\\u52a9\\u624b\\u63d2\\u4ef6\\u7684\\u529f\\u80fd\\uff0c\\u4e0d\\u662f\\u767e\\u5ea6\\u8d34\\u5427\\u539f\\u6709\\u529f\\u80fd\\uff5e\\u3002\\u8bbe\\u7f6e\\u79f0\\u547c\\u540e\\uff0c\\u5728\\u5e16\\u5b50\\u9875\\u9762\\uff0c\\u60a8\\u8bbe\\u7f6e\\u7684\\u79f0\\u547c\\u5c06\\u4f1a\\u81ea\\u52a8\\u663e\\u793a\\u5728\\u81ea\\u5df1\\u7684\\u5934\\u50cf\\u4e0a\\u3002")
assert(ascii2native("\\u58f0\\u660e\\uff1a\\u6b64\\u4e3a\\u8d34\\u5427\\u52a9\\u624b\\u63d2\\u4ef6\\u7684\\u529f\\u80fd\\uff0c\\u4e0d\\u662f\\u767e\\u5ea6\\u8d34\\u5427\\u539f\\u6709\\u529f\\u80fd\\uff5e\\u3002\\u8bbe\\u7f6e\\u79f0\\u547c\\u540e\\uff0c\\u5728\\u5e16\\u5b50\\u9875\\u9762\\uff0c\\u60a8\\u8bbe\\u7f6e\\u7684\\u79f0\\u547c\\u5c06\\u4f1a\\u81ea\\u52a8\\u663e\\u793a\\u5728\\u81ea\\u5df1\\u7684\\u5934\\u50cf\\u4e0a\\u3002").toString == "声明：此为贴吧助手插件的功能，不是百度贴吧原有功能～。设置称呼后，在帖子页面，您设置的称呼将会自动显示在自己的头像上。")
```


尝试了一下，发现自己基础很差，实现起来很困难。。。于是做了下面的事情

首先详细检查了一下unicode的定义：

http://baike.baidu.com/item/unicode
unicode是一个大字典
utf8 表示一个字符可能的字节数：1到6
utf16 表示一个字符可能的字节数：2到4，需要有两个字节表示字节序https://en.wikipedia.org/wiki/Byte_order_mark

中文范围 4E00-9FBF：CJK 统一表意符号 (CJK Unified Ideographs)
汉字收录2000多个
https://en.wikipedia.org/wiki/CJK_Unified_Ideographs

http://www.ruanyifeng.com/blog/2007/10/ascii_unicode_and_utf-8.html

java内部使用 Unicode 规范，由于大部分字体都可以由16位表示，所以java中char定义为固定宽度的 16 位实体，处理简单，不过也要看到对ascii来说，内存消耗增加一倍
http://blog.csdn.net/darxin/article/details/5079242
https://www.ibm.com/developerworks/cn/java/j-lo-chinesecoding/
https://docs.oracle.com/javase/tutorial/i18n/text/string.html


然后用java实现了一版：

```

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class StringTest {

  public static void main(String[] args) throws UnsupportedEncodingException {
    System.out.println("stack : " + bytesToHex("abc中国人".getBytes("unicode")));

    System.out.println(native2ascii("中abc").toString());// == "\\u4e2dabc");
    System.out.println(ascii2native("\\u4e2dabc").toString());// == "中abc");
    System.out.println(native2ascii("路·").toString());

    System.out.println(native2ascii("中abc").toString().equals("\\u4e2dabc"));
    System.out.println(ascii2native("\\u4e2dabc").toString().equals("中abc"));
    System.out.println(native2ascii("路·").toString().equals("\\u8def\\u00b7"));
    System.out.println(ascii2native("\\u8def\\u00b7").toString().equals("路·"));
    System.out
        .println(native2ascii("声明：此为贴吧助手插件的功能，不是百度贴吧原有功能～。设置称呼后，在帖子页面，您设置的称呼将会自动显示在自己的头像上。")
            .toString()
            .equals(
                "\\u58f0\\u660e\\uff1a\\u6b64\\u4e3a\\u8d34\\u5427\\u52a9\\u624b\\u63d2\\u4ef6\\u7684\\u529f\\u80fd\\uff0c\\u4e0d\\u662f\\u767e\\u5ea6\\u8d34\\u5427\\u539f\\u6709\\u529f\\u80fd\\uff5e\\u3002\\u8bbe\\u7f6e\\u79f0\\u547c\\u540e\\uff0c\\u5728\\u5e16\\u5b50\\u9875\\u9762\\uff0c\\u60a8\\u8bbe\\u7f6e\\u7684\\u79f0\\u547c\\u5c06\\u4f1a\\u81ea\\u52a8\\u663e\\u793a\\u5728\\u81ea\\u5df1\\u7684\\u5934\\u50cf\\u4e0a\\u3002"));
    System.out
        .println(ascii2native(
            "\\u58f0\\u660e\\uff1a\\u6b64\\u4e3a\\u8d34\\u5427\\u52a9\\u624b\\u63d2\\u4ef6\\u7684\\u529f\\u80fd\\uff0c\\u4e0d\\u662f\\u767e\\u5ea6\\u8d34\\u5427\\u539f\\u6709\\u529f\\u80fd\\uff5e\\u3002\\u8bbe\\u7f6e\\u79f0\\u547c\\u540e\\uff0c\\u5728\\u5e16\\u5b50\\u9875\\u9762\\uff0c\\u60a8\\u8bbe\\u7f6e\\u7684\\u79f0\\u547c\\u5c06\\u4f1a\\u81ea\\u52a8\\u663e\\u793a\\u5728\\u81ea\\u5df1\\u7684\\u5934\\u50cf\\u4e0a\\u3002")
            .toString().equals("声明：此为贴吧助手插件的功能，不是百度贴吧原有功能～。设置称呼后，在帖子页面，您设置的称呼将会自动显示在自己的头像上。"));

  }

  public static CharSequence native2ascii(CharSequence unicodestr) {
    int[] chars = unicodestr.chars().toArray();
    StringBuilder sb = new StringBuilder();
    for (int c : chars) {
      // if (c >= 0 && c <= 255)
      if (Charset.forName("US-ASCII").newEncoder().canEncode(((char) c) + ""))
        sb.append((char) c);
      else
        sb.append(uint16ToHex(c));
    }
    return sb.toString();
  }

  public static CharSequence ascii2native(CharSequence ascii) throws UnsupportedEncodingException {
    int[] chars = ascii.chars().toArray();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < chars.length;) {
      if (chars[i] == '\\' && chars[i + 1] == 'u') {
        byte[] bytes =
            new byte[] {
                (byte) ((Character.digit(chars[i + 2], 16) << 4) + Character
                    .digit(chars[i + 3], 16)),
                (byte) ((Character.digit(chars[i + 4], 16) << 4) + Character
                    .digit(chars[i + 5], 16))
            // 下面的写法是错误的
            // (byte) (chars[i + 2] << 4 | chars[i + 3]),(byte) (chars[i + 4] << 4 | chars[i + 5])
            };
        sb.append(new String(bytes, "Unicode"));
        i += 6;
      } else {
        sb.append((char) chars[i]);
        i++;
      }
    }
    return sb.toString();
  }

  // http://stackoverflow.com/questions/2183240/java-integer-to-byte-array
  public static final byte[] intToByteArray(int value) {
    return ByteBuffer.allocate(4).putInt(value).array();
  }

  // http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
  public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] =
          (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
    }
    return data;
  }

  // http://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
  final protected static char[] hexArray = "0123456789abcdef".toCharArray();

  public static String uint16ToHex(int unicode) {
    byte[] bytes = new byte[] {(byte) (unicode >>> 8), (byte) (unicode & 0xFF)};
    char[] hexChars = new char[] {'\\', 'u', 0, 0, 0, 0};
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2 + 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 3] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }

  public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 5];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 5] = '0';
      hexChars[j * 5 + 1] = 'x';
      hexChars[j * 5 + 2] = hexArray[v >>> 4];
      hexChars[j * 5 + 3] = hexArray[v & 0x0F];
      hexChars[j * 5 + 4] = ' ';
    }
    return new String(hexChars);
  }
}
```

回看scala的实现：

```
object CharSeq extends App {

  def native2ascii(native: CharSequence): CharSequence = native.toString.map {
    case c if isAscii(c) => c.toChar
    case u => "\\u" + Integer.toHexString(u | 0x10000).substring(1)
  }.mkString("")

  def ascii2native(ascii: CharSequence): CharSequence = {
    def ascii2nativeList(ascii: List[Char]): List[Char] = ascii match {
      case Nil => Nil
      case 92 :: 117 :: a :: b :: c :: d :: rest =>
        val char = (parseHex(a) << 12 | parseHex(b) << 8 | parseHex(c) << 4 | parseHex(d)).toChar
        char :: ascii2nativeList(rest)
      case c :: rest => c :: ascii2nativeList(rest)
    }
    ascii2nativeList(ascii.toString.toList).mkString("")
  }

  def parseHex(c: Char) = Integer.parseInt(c + "", 16)

  def isAscii(char: Char): Boolean = Charset.forName("US-ASCII").newEncoder().canEncode(char.toString)

```

其中有些细节参考了Freewind的实现 https://github.com/freewind/native2ascii
