/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.swing;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author eleandro
 */
public class JHtmlTextArea extends JEditorPane {

    public JHtmlTextArea() {
        setEditorKit(new HTMLEditorKit());
        setContentType("text/html");
    }
}
