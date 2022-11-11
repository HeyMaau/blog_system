package cn.manpok.blogsystem.config;

import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SolrConfig {

    @Value("${blog.system.solr.path}")
    private String baseUrl;

    @Bean
    public SolrClient createSolrClient() {
        return new HttpSolrClient.Builder(baseUrl).withConnectionTimeout(10000).withSocketTimeout(60000).build();
    }

    @Bean
    public MutableDataSet createMutableDataSet() {
        MutableDataSet options = new MutableDataSet().set(Parser.EXTENSIONS, Arrays.asList(
                //自定义扩展，为<pre>标签添加ine -numbers的cLass, 用于prism库代码左侧行号展示
                AutolinkExtension.create(),
                EmojiExtension.create(),
                StrikethroughExtension.create(),
                TaskListExtension.create(),
                TablesExtension.create(),
                TocExtension.create()
        ))      // set GitHub tabLe parsing options
                .set(TablesExtension.WITH_CAPTION, false)
                .set(TablesExtension.COLUMN_SPANS,
                        false)
                .set(TablesExtension.MIN_HEADER_ROWS, 1)
                .set(TablesExtension.MAX_HEADER_ROWS, 1)
                .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
                .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
                .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true);
        // setup emoji shortcut options
        // uncomment and change to your image directory for emoji images if you have it setup
                /*.set(EmojiExtension.ROOT_IMAGE_PATH, emojiInstallDirectory())
                .set(EmojiExtension.USE_SHORTCUT_TYPE, EmojiShortcutType.GITHUB)
                .set(EmojiExtension.USE_IMAGE_TYPE, EmojiImageType.IMAGE_ONLY);*/
        return options;
    }

    @Bean
    public Parser createParser(MutableDataSet options) {
        return Parser.builder(options).build();
    }

    @Bean
    public HtmlRenderer createHtmlRenderer(MutableDataSet options) {
        return HtmlRenderer.builder(options).build();
    }
}
