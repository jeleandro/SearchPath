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
public class MetadadosComboModel extends DefaultComboBoxModel<String> {

    public static enum Type {

        NODE, EDGE
    }

    public MetadadosComboModel(Type type) {
        super(list(type,new String[]{"1"}));
    }
    
    public MetadadosComboModel(Type type,String [] predefined){
        super(list(type,predefined));
    }

    public static String[] list(Type type,String[] predefined) {
        AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
        AttributeTable table;
        if (attributeModel == null){
            return new String[]{"Grafo não encontrado."};
        }else if (type == Type.NODE) {
            table = attributeModel.getNodeTable();
            return listMetadata(table, predefined);
        } else {
            table = attributeModel.getEdgeTable();
        }
        return listMetadata(table,predefined);
    }

    private static String[] listMetadata(AttributeTable table,String[] predefined) {
        AttributeColumn cols[] = table.getColumns();
        ArrayList<String> result = new ArrayList<String>(cols.length + 1);

        for(String p:predefined){
            result.add(p);
        }

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
