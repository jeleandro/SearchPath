/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath;

import java.util.ArrayList;
import java.util.List;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;

/**
 *
 * @author eleandro
 */
public class AttributeHelper {

    private static final Double ZERO = 0D;
    private static final Double ONE = 1D;
    private static final Double TWO = 2D;

    public static void deleteAttribute(AttributeTable table, String colName) {
        List<AttributeColumn> remove = new ArrayList<AttributeColumn>();
        for (AttributeColumn a : table.getColumns()) {
            if (a.getId().contains(colName)) {
                remove.add(a);
            }
        }
        for (AttributeColumn a : remove) {
            table.removeColumn(a);
        }
    }

    public static AttributeColumn createDouble(AttributeTable table, String colName) {
        return createDouble(table, colName, ZERO);
    }

    /**
     *
     * @param table
     * @param colName
     * @param defaultValue
     * @return
     */
    public static AttributeColumn createDouble(AttributeTable table, String colName, Double defaultValue) {
        //se existir deleta
        if (table.hasColumn(colName)) {
            table.removeColumn(table.getColumn(colName));
        }
        return table.addColumn(colName, colName, AttributeType.DOUBLE, AttributeOrigin.DATA, defaultValue);
    }
    
    public static AttributeColumn createFloatAttribute(AttributeTable table, String colName) {
        return createFloatAttribute(table, colName, Float.valueOf(0f));
    }
    
    /**
     *
     * @param table
     * @param colName
     * @param defaultValue
     * @return
     */
    public static AttributeColumn createFloatAttribute(AttributeTable table, String colName, Float defaultValue) {
        //se existir deleta
        if (table.hasColumn(colName)) {
            table.removeColumn(table.getColumn(colName));
        }
        return table.addColumn(colName, colName, AttributeType.FLOAT, AttributeOrigin.DATA, defaultValue);
    }
    
    public static void setAttValue(Node n, int attIndex, Object value){
        n.getAttributes().setValue(attIndex, value);
    }
    public static void setAttValue(Edge e, int attIndex, Object value){
        e.getAttributes().setValue(attIndex, value);
    }
    
}
