/*
 * 
 *    Copyright MBCRAFT di Marco Bagnaresi - © 2015
 *    All rights reserved - Tutti i diritti riservati
 * 
 *    Mail : info [ at ] mbcraft [ dot ] it 
 *    Web : http://www.mbcraft.it
 * 
 */

package it.mbcraft.libraries.service;

/**
 * Interfaccia utilizzata per gestire il controllo da linea di comando di un'istanza già esistente.
 * Solo comandi basilari.
 *
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public interface IManageable {

    /**
     * Reloads the current instance.
     */
    void reloadCurrentInstance();

    /**
     * Restarts the instance
     */
    void restartCurrentInstance();

    /**
     * Logs the current status to the standard output
     */
    void logCurrentStatus();

    /**
     * Stops the current instance.
     */
    void shutdownCurrentInstance();

    /**
     * Forces an update of the current instance.
     */
    void updateCurrentInstance();
}
