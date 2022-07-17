package com.yuanstack.sca.service.system.assembly.dingtalk;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: Markdown消息
 * @author: hansiyuan
 * @date: 2022/6/29 2:09 PM
 */
@Data
public class MarkdownMessage implements Message {

    private String title;

    private List<String> items = new ArrayList<String>();

    public void add(String text) {
        items.add(text);
    }

    public static String getBoldText(String text) {
        return "**" + text + "**";
    }

    public static String getItalicText(String text) {
        return "*" + text + "*";
    }

    public static String getLinkText(String text, String href) {
        return "[" + text + "](" + href + ")";
    }

    public static String getImageText(String imageUrl) {
        return "![image](" + imageUrl + ")";
    }

    public static String getHeaderText(int headerType, String text) {
        if (headerType < 1 || headerType > 6) {
            throw new IllegalArgumentException("headerType should be in [1, 6]");
        }

        StringBuilder numbers = new StringBuilder();
        for (int i = 0; i < headerType; i++) {
            numbers.append("#");
        }
        return numbers + " " + text;
    }

    public static String getReferenceText(String text) {
        return "> " + text;
    }

    public static String getOrderListText(List<String> orderItem) {
        if (orderItem.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= orderItem.size() - 1; i++) {
            sb.append(String.valueOf(i)).append(". ").append(orderItem.get(i - 1)).append("\n");
        }
        sb.append(String.valueOf(orderItem.size())).append(". ").append(orderItem.get(orderItem.size() - 1));
        return sb.toString();
    }

    public static String getUnorderListText(List<String> unorderItem) {
        if (unorderItem.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < unorderItem.size() - 1; i++) {
            sb.append("- ").append(unorderItem.get(i)).append("\n");
        }
        sb.append("- ").append(unorderItem.get(unorderItem.size() - 1));
        return sb.toString();
    }

    @Override
    public String toJsonString() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("msgtype", "markdown");

        Map<String, Object> markdown = new HashMap<String, Object>();
        markdown.put("title", title);

        StringBuilder markdownText = new StringBuilder();
        for (String item : items) {
            markdownText.append(item).append("\n");
        }
        markdown.put("text", markdownText.toString());
        result.put("markdown", markdown);

        return JSON.toJSONString(result);
    }
}
