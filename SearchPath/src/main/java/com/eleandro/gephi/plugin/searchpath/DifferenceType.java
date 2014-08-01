/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath;

/**
 *
 * @author eleandro
 */
public enum DifferenceType {

    ONE_ALL_ONE,
    ALL_VS_ALL;

    public DifferenceType valueOf(int i) {
        switch (i) {
            case 0:
                return ONE_ALL_ONE;
            case 1:
                return ALL_VS_ALL;
            default:
                return null;
        }
    }
}
