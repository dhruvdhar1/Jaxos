package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

import api.KeyValueRpcInterface;

public class ServerApp extends KeyValueRpcImpl {

  public static long appStartTime = System.currentTimeMillis();

  protected ServerApp(int port) throws RemoteException {
    try {
      KeyValueRpcInterface sort = new KeyValueRpcImpl();
      String randomName = new StringBuilder("rpc-server-").append(port).toString();
      Naming.rebind(randomName, sort);
      ServerLogger.info("Server ready!");
    } catch (RemoteException e) {
      ServerLogger.error("Error occurred while executing RPC request.");
      e.printStackTrace();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws RemoteException {
    if(args.length < 1) {
      ServerLogger.info("invalid arguments, usage: java server.ServerApp <port>");
      return;
    }
    String portStr = args[0];
    int portNum = Integer.parseInt(portStr);
    new ServerApp(portNum);
  }
}
