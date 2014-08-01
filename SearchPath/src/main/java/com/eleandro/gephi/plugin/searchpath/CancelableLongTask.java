/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eleandro.gephi.plugin.searchpath;

import org.gephi.utils.longtask.spi.LongTask;

/**
 *Interface to set a process as cancelable
 * @author eleandro
 */
public interface CancelableLongTask extends LongTask{
    public boolean isCanceled();
}