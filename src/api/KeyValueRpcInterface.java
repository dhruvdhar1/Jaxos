package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RPC interface for KeyValueServer. Interface exposes Key-value store operations and ensures
 * execution through paxos algorithm.
 */
public interface KeyValueRpcInterface extends Remote {

  /**
   * Get value from Key-value store using rpc.
   * @param key key to be used to fetch value.
   * @return value corresponding to the key.
   * @throws RemoteException occurred if rpc connection is unsuccessful.
   */
  KeyValueResponse get(String key) throws RemoteException;

  /**
   * Put value into the key-value store using rpc.
   * @param key key to be inserted.
   * @param value value associated with the key.
   * @return result of the operation.
   * @throws RemoteException occurred if rpc connection is unsuccessful.
   */
  KeyValueResponse put(String key, String value) throws RemoteException;

  /**
   * Delete value from Key-value store using rpc.
   * @param key key to be used to fetch value.
   * @return result of the operation.
   * @throws RemoteException occurred if rpc connection is unsuccessful.
   */
  KeyValueResponse delete(String key) throws RemoteException;
}
