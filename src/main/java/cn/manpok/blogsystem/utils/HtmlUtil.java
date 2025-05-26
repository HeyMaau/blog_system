package cn.manpok.blogsystem.utils;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HtmlUtil {

    @Autowired
    private Parser parser;

    @Autowired
    private HtmlRenderer renderer;

    /**
     * Markdown转HTML
     *
     * @param md
     * @return
     */
    public String md2Html(String md) {
        Node document = parser.parse(md);
        return renderer.render(document);
    }

    /**
     * HTML转纯文本
     *
     * @param html
     * @return
     */
    public String html2Text(String html) {
        return Jsoup.parse(html).text();
    }
}
