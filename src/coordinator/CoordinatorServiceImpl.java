package coordinator;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import api.CoordinatorInterface;
import api.KeyValueResponse;
import api.PaxosInterface;
import domain.Promise;
import domain.Proposal;

public class CoordinatorServiceImpl extends UnicastRemoteObject implements CoordinatorInterface {

  private final ExecutorService executor = Executors.newFixedThreadPool(ServerRegistry.getRegisteredServerNames().size() + 1);
  private final int half = CoordinatorApp.acceptorStubs.size()/2;

  protected CoordinatorServiceImpl() throws RemoteException {
    super();
  }

  private boolean sendPrepare(Proposal proposal) {
    int promisedNodes = 0;
    List<Future<Promise>> prepFutures = new ArrayList();
    for(PaxosInterface stub: CoordinatorApp.acceptorStubs) {
      Future<Promise> prepFuture = executor.submit(() -> stub.propose(proposal));
      prepFutures.add(prepFuture);
    }

    try {
      for(Future<Promise> prepFuture: prepFutures) {
        Promise result = prepFuture.get();
        if(null != result && result.getStatus().equals("200")) {
          promisedNodes++;
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }

    if(promisedNodes <= half) {
      CoordinatorLogger.error("Prepare majority could not be obtained");
      return false;
    }
    return true;
  }

  private boolean sendAccept(Proposal proposal) {
    int acceptedNodes = 0;
    List<Future<Boolean>> acceptFutures = new ArrayList();
    for(PaxosInterface stub: CoordinatorApp.acceptorStubs) {
      Future<Boolean> acceptFuture = executor.submit(() -> stub.accept(proposal));
      acceptFutures.add(acceptFuture);
    }

    try {
      for(Future<Boolean> acceptFuture: acceptFutures) {
        Boolean result = acceptFuture.get();
        if(null != result && result.booleanValue()) {
          acceptedNodes++;
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }

    if(acceptedNodes <= half) {
      CoordinatorLogger.error("Accept majority could not be obtained");
      return false;
    }
    return true;
  }

  private void sendLearn(Proposal proposal) throws RemoteException {
    for(PaxosInterface stub: CoordinatorApp.acceptorStubs) {
      stub.learn(proposal);
    }
    CoordinatorLogger.info("Commit complete!");
  }

  @Override
  public KeyValueResponse execute(Proposal proposal) throws RemoteException {
    KeyValueResponse response = new KeyValueResponse();
    response.setOperation(proposal.getOperation());
    CoordinatorLogger.info("Executing request received for proposal: " + proposal);

    //Stage1: send proposal
    CoordinatorLogger.info("Starting prepare...");
    boolean prepareResult = sendPrepare(proposal);
    if(!prepareResult) {
      CoordinatorLogger.error("Promise failed!");
      response.setSuccess(false);
      response.setErrorMsg("Prepare failed! Majority could not be reached!");
      return response;
    }

    //Phase2: send accept
    CoordinatorLogger.info("Starting acceptance...");
    boolean acceptResult = sendAccept(proposal);
    if(!acceptResult) {
      response.setSuccess(false);
      response.setErrorMsg("Accept failed! Majority could not be reached!");
      return response;
    }

    //Phase3: send learn request
    CoordinatorLogger.info("Sending learn requests");
    sendLearn(proposal);

    //final
    response.setSuccess(true);
    return response;
  }
}
