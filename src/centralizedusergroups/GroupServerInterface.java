/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedusergroups;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

/**
 *
 * @author usuario
 */
public interface GroupServerInterface extends Remote{
    //Servicios de la interfaz.
    
    /*
    Servicio usado para crear un nuevo grupo, con identificador textual "galias"
    El propietario del grupo es su creador, con alias "oalias", ubicado en 
    "ohostname". Devuelve false si ya existe un grupo con ese alias, true en 
    caso de  ́exito.
    */
    boolean createGroup(String galias, String oalias, String ohostname) throws RemoteException;
    
    /*
    Para determinar si existe un grupo con el nombre indi- cado. Devuelve true 
    si existe, false en caso contrario.
    */
    boolean isGroup(String galias) throws RemoteException;
    
    /*
    Se emplea para eliminar el grupo con identificador textual "galias". Śolo se 
    puede eliminar si el argumento "oalias" coincide con el propietario del 
    grupo. Devuelve true en caso de  ́exito, false en caso contrario.
    */
    boolean removeGroup(String galias, String oalias) throws RemoteException;
    
    /*
    Para añadir como nuevo miembro del grupo "galias" al usuario con el alias 
    "alias" indicado, que est́a ubicado en "hostname". Retorna false si ya existe 
    como miembro o cuando el grupo "galias" no existe. Retorna true en caso de 
    ́exito. Esta invocacíon seŕa bloqueante cuando las altas y bajas est ́en 
    bloqueadas. En tal caso, una vez desbloqueadas las altas y bajas podŕa 
    completarse la operacíon.
    */
    boolean addMember(String galias, String alias, String hostname) throws RemoteException;
    
    /*
    Para eliminar el miembro "alias" del grupo con alias "galias". Determinar 
    situaciones de error, y retornar false en ese caso, considerando en 
    particular que no puede eliminarse al propietario del grupo (creador del 
    grupo). Esta invocacíon seŕa bloqueante cuando las altas y bajas est́en 
    bloqueadas.
    */
    boolean removeMember(String galias, String alias) throws RemoteException;
    
    /*
    Determina si el usuario de alias indicado "alias" es miembro del grupo 
    "galias". Devuelve false si el grupo indicado no existe o no es miembro 
    del grupo.
    */
    boolean isMember(String galias, String alias) throws RemoteException;
    
    /*
    Devuelve el alias del propietario del grupo indicado, o null si no existe 
    dicho grupo.
    */
    String Owner(String galias) throws RemoteException;
    
    /*
    Se bloquean los intentos de añadir/eliminar miembros del grupo. Devuelve 
    false si no existe ese grupo. Esta operacíon no es bloqueante, lo que debe 
    bloquearse es la ejecución de sucesivas invocaciones de altas/bajas de 
    miembros de ese grupo hasta que se ejecute AllowMembers sobre ese grupo.
    */
    boolean StopMembers(String galias) throws RemoteException;
    
    /*
    Para permitir de nuevo las altas y bajas de miembros del grupo. Aquellos 
    que estaban bloqueados deben ser desbloqueados y prosiguen su ejecución con
    la operaci ́on que estaban realizando (alta o baja). Devuelve false si no 
    existe ese grupo.
    */
    boolean AllowMembers(String galias) throws RemoteException;
    
    /*
    Para devolver la lista de nombres de miembros de un grupo.
    */
    LinkedList<String> ListMembers(String galias) throws RemoteException;
    
    /*
    Para devolver la lista de nombres de grupos actual.
    */
    LinkedList<String> ListGroups() throws RemoteException;
    
}
