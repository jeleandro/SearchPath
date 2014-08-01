/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author eleandro
 */
public class ExcelSheet {

    private String name;
    private List<String> headers;
    private List<HashMap<String, Object>> table;

    public ExcelSheet(String name) {
        this.name = name;
        this.table = new ArrayList<HashMap<String, Object>>();
    }

    public void setHeaders(String[] headers) {
        this.headers = Arrays.asList(headers);
    }

    /**
     *
     * @param data
     * @return true if data were inserted ok
     */
    public boolean add(Object[] data) {
        if (data.length != headers.size()) {
            return false;
        }
        HashMap<String, Object> dataMap = new HashMap<String, Object>();

        int i = 0;
        for (Object o : data) {
            dataMap.put(headers.get(i++), o);
        }
        return true;
    }

    public String buildXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<ss:Worksheet ss:Name='").append(name).append("'>");
        sb.append("<ss:Table>");

        sb.append("<ss:Row>");
        for (String header : headers) {
            sb.append("<ss:Cell>").append("<ss:Data ss:Type='String'>").append(header).append("</ss:Data>").append("</ss:Cell>");
        }
        sb.append("</ss:Row>");

        for (HashMap<String, Object> data : table) {
            sb.append("<ss:Row>");
            for (String header : headers) {
                Object v = data.get(header);
                sb.append("<ss:Cell>").append("<ss:Data ss:Type='String'>").append(v.toString()).append("</ss:Data>").append("</ss:Cell>");
            }
            sb.append("</ss:Row>");
        }
        sb.append("</ss:Table>");
        sb.append("</ss:Worksheet>");
        return sb.toString();
    }
}