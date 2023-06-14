/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedusergroups;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author usuario
 */
public class CentralizedUserGroups {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException {
        // TODO code application logic here
        
        try {
            System.setProperty("java.security.policy", "/home/usuario/NetBeansProjects/CentralizedUserGroups/politicas/java.policy");
             if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            LocateRegistry.createRegistry(1099);
            GroupServer servidor = new GroupServer();
            String ip_servidor= InetAddress.getLocalHost().getHostAddress();
             
            Naming.rebind("//" + ip_servidor+ "/GroupServer", (Remote) servidor);
            System.out.println("Servidor preparado");
        } catch (RemoteException e) {
            System.err.println("Error server:" + e.toString());
            e.printStackTrace();
        } catch (MalformedURLException ex) {
            Logger.getLogger(CentralizedUserGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
