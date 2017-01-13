/*
 * 
 *    Copyright MBCRAFT di Marco Bagnaresi - © 2015
 *    All rights reserved - Tutti i diritti riservati
 * 
 *    Mail : info [ at ] mbcraft [ dot ] it 
 *    Web : http://www.mbcraft.it
 * 
 */

package it.mbcraft.libraries.process.flags;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public class MemoryFlag implements IFlag {

    //the flag in wich save the value
    private boolean myFlag;

    //the unique instance
    protected static IFlag _instance = null;

    protected MemoryFlag(boolean initialValue) {
        myFlag = initialValue;
    }

    @Override
    public boolean isSet() {
        return myFlag == true;
    }

    @Override
    public boolean isReset() {
        return myFlag == false;
    }

    @Override
    public void reset() {
        myFlag = false;
    }

    @Override
    public void set() {
        myFlag = true;
    }

}
