package chat;

import application.Controller;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class ChatClient {
    ChatServiceImpl chat;
    Registry registry;
    ChatService stub;
    List<String> messages=new ArrayList();
    ChatService server;
    Controller con;
    public ChatClient(Controller Con) throws RemoteException{
        con=Con;
        start();
    }
    
    void start() throws RemoteException{
        chat = new ChatServiceImpl("Client",con);
        registry = LocateRegistry.getRegistry();
        stub = (ChatService) UnicastRemoteObject.exportObject(chat, 0);
        registry.rebind("client", stub);
        System.out.println("client ready, searching for server...");
    }
    public void sendMessage(String msg) throws RemoteException{
            try {
                ChatService server = getServer(registry);
                if (server != null) {
                    server.send(msg);
                }
            } catch (NotBoundException ex) {
                System.out.println("still no server...");
            } catch (AccessException ex) {
                ex.printStackTrace();
            } catch (NamingException e) {
                e.printStackTrace();
            }
    }
    public List<String> getAllMessages() throws RemoteException{
        return chat.getAllMessages();
    }
    private static ChatService getServer(Registry registry) throws RemoteException, NotBoundException, NamingException {
        if (false) {
            // lookup direktno preko registry objekta
            return (ChatService) registry.lookup("server");
        } else {
            // lookup preko JNDIa, bez da moramo imati referencu na registry objekt
            final Hashtable jndiProperties = new Hashtable();
            jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
            jndiProperties.put(Context.PROVIDER_URL, "rmi://localhost:1099");

            InitialContext ctx = new InitialContext(jndiProperties);
            ChatService server = (ChatService) ctx.lookup("server");
            ctx.close();
            return server;
        }
    }
}
