package org.cas.client.platform.bar.netbeans;

public class NetUtil {
	
    public static String fetchProductPrice(
            String description,
            String moneyLetter) {
        if (description == null || description.trim().length() == 0) {
            return "";
        }
        int p = description.lastIndexOf(moneyLetter);
        String html = p > 0 ? description.substring(p + 1) : description;
        return trimContentFromHTML(html);
    }

    // return the trimed content from html format text.
    public static String trimContentFromHTML(
            String html) {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int start = html.indexOf("<");
            int end = html.indexOf(">");
            if (start < 0) {
                break;
            } else if (end > start) {
                sb.append(html.substring(0, start));
                html = html.substring(end + 1);
            }
        }

        html = sb.toString() + html;
        while (html.startsWith("&nbsp;")) {
            html = html.substring(6).trim();
        }
        while (html.endsWith("&nbsp;")) {
            html = html.substring(0, html.length() - 6).trim();
        }

        return html;
    }
}
