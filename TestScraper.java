import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class TestScraper {
    public static void main(String[] args) throws Exception {
        String searchUrl = "https://search.yahoo.com/search?p=cyberpunk+system+requirements";
        Document doc = Jsoup.connect(searchUrl).userAgent("Mozilla/5.0").get();
        Elements snippets = doc.select(".compText");
        System.out.println("Found " + snippets.size() + " snippets.");
        System.out.println(snippets.text().substring(0, Math.min(500, snippets.text().length())));
    }
}
