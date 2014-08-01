/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath;

import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeTable;
import org.openide.util.Lookup;

/**
 *
 * @author eleandro
 */
public class MetadadosModel extends DefaultComboBoxModel<String> {

    public static enum Type {

        NODE, EDGE
    }

    public MetadadosModel(Type type) {
        super(list(type));
    }

    public static String[] list(Type type) {
        AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
        AttributeTable table;
        if (type == Type.NODE) {
            table = attributeModel.getEdgeTable();
            return listMetadata(table);
        } else {
            table = attributeModel.getEdgeTable();
        }
        return listMetadata(table);
    }

    private static String[] listMetadata(AttributeTable table) {
        AttributeColumn cols[] = table.getColumns();
        ArrayList<String> result = new ArrayList<String>(cols.length + 1);

        result.add("1");

        for (AttributeColumn c : cols) {
            switch (c.getType()) {
                case BYTE:
                case SHORT:
                case LONG:
                case INT:
                case FLOAT:
                case DOUBLE:
                case BIGINTEGER:
                case BIGDECIMAL:
                    result.add(c.getTitle());

            }
        }

        return (String[]) result.toArray(new String[cols.length + 1]);
    }
}
