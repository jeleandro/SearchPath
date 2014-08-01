/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.report;

/**
 *
 * @author eleandro
 */
public class HtmlTable {
    private StringBuilder sb = new StringBuilder();

    private HtmlTable addRow(String lineAttributes, String tag, Object[] a) {

        if (lineAttributes != null && lineAttributes.length() > 0) {
            sb.append("<tr ").append(lineAttributes).append(">");
        } else {
            sb.append("<tr>");
        }
        for (int i = 0; i < a.length; i++) {
            sb
                .append("<")
                .append(tag)
                .append(">")
                .append(a[i].toString())
                .append("</")
                .append(tag)
                .append(">");
        }
        sb.append("</tr>\r\n");
        return this;
    }

    public HtmlTable addData(String lineAttributes, Object[] a) {
        return addRow(lineAttributes, "td", a);
    }

    public HtmlTable addHeader(String lineAttributes, Object[] a) {
        return addRow(lineAttributes, "th", a);
    }
    public String build(String tableAttributes){
        return
             new StringBuilder("<table ")
                .append((tableAttributes!=null)?tableAttributes:"")
                .append(">")
                .append(sb)
                .append("</table>")
                .toString();        
        
    }
}
