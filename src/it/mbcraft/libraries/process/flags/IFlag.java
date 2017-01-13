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
 * Questa interfaccia viene utilizzata per gestire un flag vero/falso
 * senza preoccuparsi della memorizzazione di esso.
 *
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public interface IFlag {

    boolean isSet();

    boolean isReset();

    void reset();

    void set();

}
