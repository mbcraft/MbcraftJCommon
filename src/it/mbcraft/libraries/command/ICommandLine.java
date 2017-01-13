/*
 * 
 *    Copyright MBCRAFT di Marco Bagnaresi - © 2015
 *    All rights reserved - Tutti i diritti riservati
 * 
 *    Mail : info [ at ] mbcraft [ dot ] it 
 *    Web : http://www.mbcraft.it
 * 
 */

package it.mbcraft.libraries.command;

/**
 * @author Marco Bagnaresi <marco.bagnaresi@gmail.com>
 */
public interface ICommandLine {
    /**
     * Ritorna il comando per effettuare questo task.
     *
     * @return La linea di comando completa di tutti i parametri
     */
    String getFullCommandLine();

    /**
     * Ritorna l'elenco delle variabili d'ambiente da utilizzare.
     *
     * @return L'array delle variabili d'ambiente da utilizzare.
     */
    String[] getEnvParams();
}
