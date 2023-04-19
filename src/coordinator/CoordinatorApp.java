package coordinator;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import api.CoordinatorInterface;
import api.KeyValueRpcInterface;
import api.PaxosInterface;

public class CoordinatorApp extends CoordinatorServiceImpl {

  public static List<KeyValueRpcInterface> keyValueServerStubs;
  public static List<PaxosInterface> acceptorStubs;

  protected CoordinatorApp() throws RemoteException {
    try {
      CoordinatorInterface coordinatorObj = new CoordinatorServiceImpl();
      Naming.rebind("coordinator", coordinatorObj);
      CoordinatorLogger.info("Coordinator started!");
    } catch (RemoteException e) {
      CoordinatorLogger.error("Error occurred while executing RPC request.");;
      e.printStackTrace();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {
    keyValueServerStubs = new ArrayList<>();
    acceptorStubs = new ArrayList<>();
    List<String> registeredServers = ServerRegistry.getRegisteredServerNames();

    for(String name: registeredServers) {
      KeyValueRpcInterface keyValStub = (KeyValueRpcInterface) Naming.lookup("//localhost/"+name);
      PaxosInterface paxosStub = (PaxosInterface) Naming.lookup("//localhost/"+name);
      keyValueServerStubs.add(keyValStub);
      acceptorStubs.add(paxosStub);
    }
    new CoordinatorApp();
  }
}
