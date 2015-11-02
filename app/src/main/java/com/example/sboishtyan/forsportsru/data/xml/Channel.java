package com.example.sboishtyan.forsportsru.data.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Root(strict = false)
public class Channel {

    private static final SimpleDateFormat format = new SimpleDateFormat("dd.mm.yy");
    @ElementList(inline = true) private List<Item> items;

    public List<Item> getItems() {
        if (items == null) items = new ArrayList<>();
        return items;
    }

    @Root(strict = false)
    public static class Item {
        @Element
        public String title;
        @Element
        public String link;
        @Element
        public String pubDate;

        public Item(
                @Element(name = "title") String title,
                @Element(name = "link") String link,
                @Element(name = "pubDate") String pubDate) {
            this.title = formatTitle(title);
            this.link = format(link);
            this.pubDate = format(pubDate);
        }

        public String formatTitle(String title) {
            return format(title)
                        .replaceAll("&laquo;", "\"")
                        .replaceAll("&raquo;", "\"")
                        .replaceAll("&ndash;", "-");
        }

        public String format(String string) {
            return string.replaceAll("\n", "");
        }

        public String getFullLink() {
            return String.format("http://%s", link);
        }
    }
}
