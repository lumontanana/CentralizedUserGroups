/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedusergroups;

import java.io.Serializable;

/**
 *
 * @author usuario
 */
public class GroupMember implements Serializable{
    
    String nombre_miembro;
    String hostname;
    
    public GroupMember(String nombre_miembro, String hostname){
        this.nombre_miembro=nombre_miembro;
        this.hostname=hostname;
    }
    
}
