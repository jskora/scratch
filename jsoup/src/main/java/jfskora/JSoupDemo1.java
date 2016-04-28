package jfskora;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;

public class JSoupDemo1
{
    public static void main( String[] args ) throws IOException {
        System.out.println("reading CNN Top Stories");
        System.out.println("----------------------------------------");
        readTopStories();
    }

    private static void readTopStories() throws IOException {
//        Document doc = Jsoup.connect("http://rss.cnn.com/rss/cnn_topstories.rss").get();
        String url = "http://rss.cnn.com/rss/cnn_topstories.rss";
        File xmlSource = new File(JSoupDemo1.class.getClassLoader().getResource("cnn_topstories.rss").getFile());
        Document doc = Jsoup.parse(xmlSource, "UTF-8", url);
        System.out.println("channels");
        for (Element element : doc.getElementsByTag("channel")) {
            System.out.println("  " + element.getElementsByTag("title").first().text());
            System.out.println("    " + element.getElementsByTag("description").first().text());
            System.out.println("    " + element.getElementsByTag("pubDate").first().text());
            for (Element item : doc.getElementsByTag("item")) {
                System.out.println("------------------------------------------------------------");
                System.out.println("    " + item.getElementsByTag("title").text());
                System.out.println("      " + item.getElementsByTag("description").text());
                System.out.println("      " + item.getElementsByTag("pubDate").first().text());
            }
        }

    }

    private static void printTags(Element top, String indent) {
        for (Element element : top.getAllElements()) {
            System.out.println("tag=" + element.tagName());
//            printTags(element, indent + "  ");
        }
    }
}
