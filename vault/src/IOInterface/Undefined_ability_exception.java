/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package IOInterface;

/**
 *
 * @author Jonas Huber <Jonas_Huber2@gmx.de>
 */
public class Undefined_ability_exception extends Exception {
 
    private static final long serialVersionUID = 752642359235928L;
    
        Undefined_ability_exception(){
            super("PIN Ability is not defined");
        }
    
}
