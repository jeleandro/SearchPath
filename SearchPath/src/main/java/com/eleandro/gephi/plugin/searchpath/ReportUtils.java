/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath;

import java.util.List;
import org.gephi.graph.api.Node;

/**
 *
 * @author eleandro
 */
public class ReportUtils {

    public static String listToHTML(List<String> messages, String tag) {
        if (messages.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("<").append(tag).append(">\r\n");
            for (String m : messages) {
                sb.append("<li>").append(m).append("</li>\r\n");
            }
            sb.append("</").append(tag).append(">\r\n");
            return sb.toString();
        }
        return "";
    }
    
    public static String listNodeToCsv(List<Node> list){
        StringBuilder sb = new StringBuilder();
         for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).getNodeData().getId());
            if (i < list.size() - 1) {
                sb.append(';');
            }
        }
        return sb.toString();
    }
}