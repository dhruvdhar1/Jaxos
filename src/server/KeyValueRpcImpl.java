package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

import api.CoordinatorInterface;
import api.KeyValueResponse;
import api.KeyValueRpcInterface;
import api.PaxosInterface;
import domain.Promise;
import domain.Proposal;


public class KeyValueRpcImpl extends UnicastRemoteObject implements KeyValueRpcInterface, PaxosInterface {

  private CoordinatorInterface coordinator;
  private int maxId;

  protected KeyValueRpcImpl() throws RemoteException {
    super();
  }

  private void init() throws RemoteException {
    try {
      ServerLogger.info("setting up connection with the coordinator");
      coordinator = (CoordinatorInterface) Naming.lookup("//localhost/coordinator");
    } catch (NotBoundException e) {
      ServerLogger.error("Coordinator connection unsuccessful. Coordinator not bound to rmiregistry");
      e.printStackTrace();
    } catch (MalformedURLException e) {
      ServerLogger.error("Coordinator connection unsuccessful. Coordinator url may be malformed.");
      e.printStackTrace();
    }
  }

  @Override
  public KeyValueResponse get(String key) throws RemoteException {
    String value =  KeyValue.getInstance().get(key);
    KeyValueResponse response = new KeyValueResponse();
    response.setOperation("GET");
    if(null == value) {
      response.setErrorMsg("Key not found");
      response.setSuccess(false);
    } else {
      response.setSuccess(true);
      response.setValue(value);
    }
    return response;
  }

  @Override
  public KeyValueResponse put(String key, String value) throws RemoteException {
    if(coordinator == null) {
      init();
    }
    Proposal generatedProposal = Proposal.generateProposal("PUT", key, value);
    return coordinator.execute(generatedProposal);
  }

  @Override
  public KeyValueResponse delete(String key) throws RemoteException {
    if(coordinator == null) {
      init();
    }
    Proposal generatedProposal = Proposal.generateProposal("DELETE", key, null);
    return coordinator.execute(generatedProposal);
  }

  @Override
  public Promise propose(Proposal proposal) throws RemoteException {
    ServerLogger.info("Propose request received");
    if(new Random().nextInt(10) <= 1) {
      //emulating failure
      return null;
    }

    //Adding random delay < 1s to emulate network delays
    int randomDelay = (int)((Math.random())  * 1000);
    System.out.println("Random delay: " + randomDelay);
    try {
      Thread.sleep(randomDelay);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    Promise response = new Promise();
    if(proposal.getProposalId() > this.maxId) {
      this.maxId = proposal.getProposalId();
      ServerLogger.info("Setting new max id: " + this.maxId);
      ServerLogger.info("Propose complete: " + proposal);
      response.setStatus("200");
    } else {
      ServerLogger.error("Propose rejected: " + proposal);
      response.setStatus("500");
    }

    return response;
  }

  @Override
  public Boolean accept(Proposal proposal) throws RemoteException {
    ServerLogger.info("Accept request received");
    if(new Random().nextInt(10) <= 1) {
      //emulating failure
      return null;
    }
    if(proposal.getProposalId() != this.maxId) {
      ServerLogger.error("Accept failed: " + proposal);
      return Boolean.FALSE;
    } else {
      ServerLogger.info("Accept completed: " + proposal);
      return Boolean.TRUE;
    }
  }

  @Override
  public void learn(Proposal proposal) throws RemoteException {
    ServerLogger.info("Learn request received");
    String key = proposal.getKey();
    String value = proposal.getValue();
    String operation = proposal.getOperation();

    if(operation.equals("PUT")) {
      KeyValue.getInstance().put(key, value);
    } else if(operation.equals("DELETE")) {
      KeyValue.getInstance().delete(key);
    }
    ServerLogger.info("Learn operation completed.");
  }
}
