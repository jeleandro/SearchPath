/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath;

/**
 * interface para conter os nomes das colunas a serem criadas
 * @author José Eleandro Custódio
 */

public interface ColumnConstants {
    public static final String LINK_COUNT_NAME = "linkcount";
    public static final String NODE_PAIR_NAME = "nodepair";
    
    /**
     * marcação de ciclos
     */
    public static final String CICLE  = "Cicle";
    
    /**
     * Quantidade de nós que estão na árvore de entrada
     */
    public static final String NP_FROM_TO = "node_pair_from_to";
    
    /**
     * Quantidade de nós que estao a frente.
     */
    public static final String NP_TO_FROM = "node_pair_to_from";
    
    public static final String PATH_REF = "REFERENCIA_CAMINHO";
    
    public static final String SPLC_PATH  = "SPLC_PATH";
    public static final String SPNP_PATH  = "SPNP_PATH";
    
    public static final String SPLC_PATH_MERGE = "SPLC_PATH_MERGE";
    public static final String SPNP_PATH_MERGE = "SPNP_PATH_MERGE";
    
    public static final String SPLC_RELEVANCE = "SPLC_RELEVANCE";
}
