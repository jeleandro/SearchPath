/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

/**
 *
 * @author eleandro
 */
public class CenteredFrame extends JFrame {

    public CenteredFrame(String title, Container c) {
        super(title);
        this.setContentPane(c);
        toCenter();
    }
    
    @Override
    public void setSize(int width, int height){
        super.setSize(width,height);
        toCenter();
    }
    
    @Override
    public void setSize(Dimension d){
        super.setSize(d);
        toCenter();
    }

    public void toCenter() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }
}
