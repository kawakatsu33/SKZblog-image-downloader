import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

public class SKZblogImageDownloader {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter URL: ");
        String url = scanner.nextLine();
        scanner.close(); 

        try {
            String saveFolder = createFolderNameFromUrl(url);
            downloadImagesFromUrl(url, saveFolder);
        } catch (IOException e) {
            System.out.println("エラー：" + e.getMessage());
        }
    }

    public static String createFolderNameFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Element yearElement = doc.selectFirst("div.ym-inner span.ym-year");
        Element monthElement = doc.selectFirst("div.ym-inner span.ym-month");
        Element dateElement = doc.selectFirst("div.ym-inner p.date.wf-a");
        Element nameElement = doc.selectFirst("dl.prof dt.name");
        Element titleElement = doc.selectFirst("div.inner.title-wrap h1.title");

        if (yearElement != null && monthElement != null && dateElement != null && nameElement != null && titleElement != null) {
            String year = yearElement.text();
            String month = monthElement.text();
            String date = dateElement.text();
            String name = nameElement.ownText();
            String title = titleElement.text();
            return "./save-images/" + year + "." + month + "." + date + "-" + name + "「" + title + "」";
        } else {
            throw new IOException("情報が見つかりません");
        }
    }

    public static void downloadImagesFromUrl(String url, String saveFolder) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements imgTags = doc.select("div.box-article img");

        File dir = new File(saveFolder);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (Element img : imgTags) {
            String imgUrl = img.absUrl("src");
            if (imgUrl.isEmpty()) {
                continue;
            }

            try {
                URI uri = new URI(imgUrl);
                URL imageURL = uri.toURL();
                String fileName = saveFolder + "/" + imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
                FileUtils.copyURLToFile(imageURL, new File(fileName));
                System.out.println("保存: " + fileName);
            } catch (URISyntaxException e) {
                System.out.println("URI構文エラー: " + imgUrl + ": " + e.getMessage());
            } catch (IOException e) {
                System.out.println("保存エラー: " + imgUrl + ": " + e.getMessage());
            }
        }
    }
}
