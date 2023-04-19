package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

import domain.Proposal;

/**
 * Interface provides method to initiate communication with the coordinator
 */
public interface CoordinatorInterface extends Remote {

  /**
   * Executes the operation specified in the proposal using paxos algorithm
   * @param proposal proposal
   * @return  result of the executed operation
   * @throws RemoteException if RPC communication fails
   */
  KeyValueResponse execute(Proposal proposal) throws RemoteException;
}
