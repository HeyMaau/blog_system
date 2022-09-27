package cn.manpok.blogsystem.utils;

import org.springframework.lang.Nullable;

public class TextUtil {

    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }
}
