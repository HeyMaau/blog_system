package cn.manpok.blogsystem.test;

import java.util.Arrays;

public class TestString {

    public static void main(String[] args) {
        String str = "abcdef";
        String substring = str.substring(2);
        System.out.println(substring);

        String str1 = "abc";
        String[] split = str1.split("-");
        System.out.println(Arrays.toString(split));
    }
}
