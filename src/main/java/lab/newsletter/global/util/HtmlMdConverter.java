package lab.newsletter.global.util;

import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class HtmlMdConverter {

    private final MutableDataSet OPTS = new MutableDataSet().set(Parser.EXTENSIONS,
            List.of(AutolinkExtension.create(), FootnoteExtension.create()));

    private final Parser parser = Parser.builder(OPTS).build();
    private final HtmlRenderer renderer = HtmlRenderer.builder(OPTS).build();

    public String toHtml(String md) {
        if (md == null || md.isEmpty()) {
            throw new IllegalArgumentException("MD cannot be null or empty");
        }
        Node doc = parser.parse(md);
        return renderer.render(doc);
    }
}
