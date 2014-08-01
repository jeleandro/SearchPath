/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.report;

import java.util.ArrayList;


/**
 *
 * @author eleandro
 */
public class ExcelXMLCreator {

    private ArrayList<ExcelSheet> sheets;
    
    public ExcelXMLCreator(){
        sheets = new ArrayList<ExcelSheet>();
    }

    public String buildSheets() {
        StringBuilder sb = new StringBuilder();
        for (ExcelSheet s : sheets) {
            sb.append(s.buildXML());
        }

        return sb.toString();
    }
    
    public ExcelSheet createSheet(String name){
        ExcelSheet sheet = new ExcelSheet(name);
        sheets.add(sheet);
        return sheet;
    }

    public String buildXML() {
        return "<?xml version=\"1.0\"?><ss:Workbook xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\">"
                + buildSheets()
                + "</ss:Workbook>";
    }
}
