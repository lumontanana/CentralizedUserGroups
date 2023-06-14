/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedusergroups;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author usuario
 */
public class GroupServer extends UnicastRemoteObject implements GroupServerInterface {

    LinkedList<Group> lista_grupos = new LinkedList();
    ReentrantLock mylock = new ReentrantLock();
 
    
    public GroupServer() throws RemoteException{ 
        super();
    }
    
    public class Group{
        String nombre_grupo;
        String nombre_propietario;
        LinkedList<GroupMember> lista_miembros;
        String hostname;
        boolean bloqueo;
        Condition mutex;
        
        public Group(String nombre_grupo, String nombre_propietario, LinkedList<GroupMember> lista_miembros,String hostname){
            this.nombre_grupo = nombre_grupo;
            this.nombre_propietario = nombre_propietario;
            this.lista_miembros = lista_miembros;
            this.hostname=hostname;
            this.bloqueo = false;
            this.mutex = mylock.newCondition();
            
            //Creas un miembro inicial, que será el propietario
            GroupMember miembro_inicial=new GroupMember(this.nombre_propietario,this.hostname);
            lista_miembros.add(miembro_inicial);
        }
        
    }

    @Override
    public boolean createGroup(String galias, String oalias, String ohostname) throws RemoteException{
            
        mylock.lock();
        
        if(this.isGroup(galias)){
            mylock.unlock();
            return false;
        }
        
         
        LinkedList<GroupMember> miembros_vacios = new LinkedList<>();
        
        Group nuevo_grupo= new Group(galias,oalias,miembros_vacios,ohostname); //Creamos el grupo si no existe
        
        this.lista_grupos.add(nuevo_grupo);
        mylock.unlock();
        return true;  
            
    }

    @Override
    public boolean isGroup(String galias) throws RemoteException{
       mylock.lock();
        
       for(int i=0; i<lista_grupos.size();i++){
            if(lista_grupos.get(i).nombre_grupo.equals(galias)){
                mylock.unlock();
                return true;
            }
        }
        mylock.unlock();
        return false;
        
       
        
       
    }

    @Override
    public boolean removeGroup(String galias, String oalias) throws RemoteException{    
        
        mylock.lock();
        
        if(!this.isGroup(galias)){
            mylock.unlock();
            return false;
        }
            
        int pos = 0;
        for(int i = 0; i < this.lista_grupos.size(); i++){
            if(this.lista_grupos.get(i).nombre_grupo.equals(galias)){
                if(this.lista_grupos.get(i).nombre_propietario.equals(oalias)){
                    pos = i;    
                    break;
                }
                mylock.unlock();
                return false;
            }
        }
                
        this.lista_grupos.remove(pos);
        mylock.unlock();
        
        return true;
            
    }

    @Override
    public boolean addMember(String galias, String alias, String hostname) throws RemoteException{    
        //BLOCK
        
        mylock.lock();
        
        try{

            if(!isGroup(galias)){
                mylock.unlock();
                return false;
            }
            
            if(isMember(galias,alias)){
                mylock.unlock();
                return false;
       }
               
            for(int i = 0; i < this.lista_grupos.size(); i++){   
                if(this.lista_grupos.get(i).nombre_grupo.equals(galias)){  
                      
                    while(this.lista_grupos.get(i).bloqueo){
                        this.lista_grupos.get(i).mutex.await();
                    }
                    GroupMember nuevomiembro=new GroupMember(alias, hostname);
                    this.lista_grupos.get(i).lista_miembros.add(nuevomiembro);
                    mylock.unlock();
                    return true;
                }
            }
 
            mylock.unlock();
            return false;
        
        }catch(InterruptedException ex){     
            System.out.println(Arrays.toString(ex.getStackTrace()));
            mylock.unlock();
            return false;
            
        }

    }

    @Override
    public boolean removeMember(String galias, String alias) throws RemoteException{    
        
        //BLOCK
        mylock.lock();
        try{
            
            if(!isGroup(galias)){
                mylock.unlock();
                return false;
            }
            
            if(!isMember(galias,alias)){
                mylock.unlock();
                return false;
       }
            if(Owner(galias).equals(alias)){
                mylock.unlock();
                return false;
            }
               
            for(int i = 0; i < this.lista_grupos.size(); i++){   
                if(this.lista_grupos.get(i).nombre_grupo.equals(galias)){ 
                   
                    while(this.lista_grupos.get(i).bloqueo){
                        this.lista_grupos.get(i).mutex.await();                        
                    }
                    
                    for(int j = 0; j < this.lista_grupos.get(i).lista_miembros.size(); j++){
                        if(this.lista_grupos.get(i).lista_miembros.get(j).nombre_miembro.equals(alias)){
                            this.lista_grupos.get(i).lista_miembros.remove(j);
                            
                            mylock.unlock();
                            return true;
                        }
                        
                    }

                    mylock.unlock();
                    return false;
                }
                
            }
            mylock.unlock();
            return false;
     
        }catch(InterruptedException ex){     
            System.out.println(Arrays.toString(ex.getStackTrace()));
            mylock.unlock();
            return false;
            
        }
        

    }

    @Override
    public boolean isMember(String galias, String alias) throws RemoteException{    
        
        mylock.lock();
        if(!this.isGroup(galias)){
            mylock.unlock();
            return false;
        }
        for (int i = 0; i < this.lista_grupos.size(); i++) {           
            if(this.lista_grupos.get(i).nombre_grupo.equals(galias)){
                for (int j = 0; j < this.lista_grupos.get(i).lista_miembros.size(); j++) {
                    if(this.lista_grupos.get(i).lista_miembros.get(j).nombre_miembro.equals(alias)){
                        mylock.unlock();
                        return true;
                    }
                    
                }
                
            }
        }
        mylock.unlock();
        return false;
        
    }
    
   
    
   
    @Override
    public String Owner(String galias) throws RemoteException{    
        
        mylock.lock();
        
        if(!isGroup(galias)){
            mylock.unlock();
            return null;
        }
        
        for(int i = 0; i < this.lista_grupos.size(); i++){
            if(this.lista_grupos.get(i).nombre_grupo.equals(galias)){
                String propietario = this.lista_grupos.get(i).nombre_propietario;
                mylock.unlock();
                return propietario;
            }
        }
        mylock.unlock();
        return null;
    }

    @Override
    public boolean StopMembers(String galias) throws RemoteException{    
        
        mylock.lock();
        
             
        if(!isGroup(galias)){
            mylock.unlock();
            return false;
        }  
            
        for(int i = 0; i < this.lista_grupos.size(); i++){
            if(this.lista_grupos.get(i).nombre_grupo.equals(galias)){
                this.lista_grupos.get(i).bloqueo = true;
                mylock.unlock();
                return true;
            } 
        }
        mylock.unlock();
        return false; 
        
    }

    @Override
    public boolean AllowMembers(String galias) throws RemoteException{    
        
        mylock.lock();
        
        if(!isGroup(galias)){
            mylock.unlock();
            return false;
        }  

        for(int i = 0; i < this.lista_grupos.size(); i++){
            if(this.lista_grupos.get(i).nombre_grupo.equals(galias)){
                this.lista_grupos.get(i).bloqueo = false;
                this.lista_grupos.get(i).mutex.signalAll();
                mylock.unlock();
                return true;
            } 
        }
        mylock.unlock();
        return false; 
            
        
    }

    @Override
    public LinkedList<String> ListMembers(String galias) throws RemoteException{   
        
       
        mylock.lock();
        
        LinkedList<String> miembros = new LinkedList<String>();
            for(int i = 0; i < this.lista_grupos.size(); i++){
                if(this.lista_grupos.get(i).nombre_grupo.equals(galias)){
                    for(int j = 0; j < this.lista_grupos.get(i).lista_miembros.size(); j++){
                        miembros.add(this.lista_grupos.get(i).lista_miembros.get(j).nombre_miembro);
                    }
                    mylock.unlock();
                    return miembros;
            }
        }
        mylock.unlock();
        return null;        
           
    }

    @Override
    public LinkedList<String> ListGroups() throws RemoteException{    
        
        mylock.lock();
        LinkedList<String> grupos = new LinkedList<String>();
        for(int i = 0; i < this.lista_grupos.size(); i++){
            grupos.add(this.lista_grupos.get(i).nombre_grupo);
        }
        mylock.unlock();
        return grupos;     
          
    }
    
    
    
}
