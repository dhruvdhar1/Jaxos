package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import api.KeyValueResponse;
import api.KeyValueRpcInterface;

public class ClientApp {

  public static boolean preloadComplete = false;

  public static void main(String[] args) {
    try {
      if(args.length < 1) {
        System.out.println("invalid arguments, usage: java ClientApp <host> <port>");
        return;
      }
      String host = args[0];
      String portStr = args[1];
      int portNum = Integer.parseInt(portStr);
      String serverName = new StringBuilder("rpc-server-").append(portNum).toString();
      KeyValueRpcInterface serverStub = (KeyValueRpcInterface) Naming.lookup("//"+host+"/"+serverName);

      handleConnection(serverStub);

    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (NotBoundException e) {
      e.printStackTrace();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  private static void handleConnection(KeyValueRpcInterface stub) throws RemoteException {
    if(!preloadComplete) {
      performInitialDataWithRpc(stub);
      preloadComplete = true;
    }
    while(true) {
      System.out.println("--Select an operation--");
      System.out.println("1. Get");
      System.out.println("2. Put");
      System.out.println("3. Delete");
      System.out.println("4. Exit");
      Scanner sc = new Scanner(System.in);
      String input = sc.nextLine();
      int userOption = Integer.parseInt(input);
      if (userOption == 4) break;
      handleUserInput(userOption, stub);
    }
  }

  private static void handleUserInput(int selectedOption, KeyValueRpcInterface stub) throws RemoteException {
    Scanner sc = new Scanner(System.in);
    String key;
    switch (selectedOption) {
      case 1:
        System.out.println("Enter the key: ");
        key = sc.nextLine();
        KeyValueResponse result = stub.get(key);
        if (result.isSuccess()) {
          System.out.println("Value = " + result.getValue());
        } else {
          System.out.println("Error! " + result.getErrorMsg());
        }
        break;
      case 2:
        System.out.println("Enter the key: ");
        key = sc.nextLine();
        System.out.println("Enter the value: ");
        String value = sc.nextLine();
        KeyValueResponse putRes = stub.put(key, value);
        if (putRes.isSuccess()) {
          System.out.println("Operation completed successfully!");
        } else {
          System.out.println("Error! " + putRes.getErrorMsg());
        }
        break;
      case 3:
        System.out.println("Enter the key: ");
        key = sc.nextLine();
        KeyValueResponse deleteResult = stub.delete(key);
        if (deleteResult.isSuccess()) {
          System.out.println("Operation completed successfully!");
        } else {
          System.out.println("Error! " + deleteResult.getErrorMsg());
        }
        break;
      default:
        System.out.println("Wrong choice! Please try again..");
        break;
    }
  }

  public static void performInitialDataWithRpc(KeyValueRpcInterface stub) throws RemoteException {

    doPut("foo", "bar", stub);
    doPut("distributed", "systems", stub);
    doPut("hello", "world", stub);
    doPut("dhruv", "dhar", stub);
    doPut("summer", "high", stub);

    ClientLogger.info("Value for key 'dhruv': " + stub.get("dhruv").getValue());
    ClientLogger.info("Value for key 'summer': " + stub.get("summer").getValue());
    ClientLogger.info("Value for key 'foo': " + stub.get("foo").getValue());
    ClientLogger.info("Value for key 'distributed': " + stub.get("distributed").getValue());
    ClientLogger.info("Value for key 'hello': " + stub.get("hello").getValue());

    doDelete("hello", stub);
    doDelete("foo", stub);
    doDelete("distributed", stub);
    doDelete("dhruv", stub);
    doDelete("summer", stub);

    doPut("foo", "bar", stub);
    doPut("distributed", "systems", stub);
    doPut("hello", "world", stub);
    doPut("dhruv", "dhar", stub);
    doPut("summer", "high", stub);
  }

  private static void doPut(String key, String value, KeyValueRpcInterface stub) throws RemoteException {
    ClientLogger.info(String.format("Inserting pair %s:%s ", key, value));
    KeyValueResponse res = stub.put(key, value);
    if(res.isSuccess()) {
      ClientLogger.info("Pair added successfully");
    } else {
      ClientLogger.error(res.getErrorMsg());
    }
  }

  private static void doDelete(String key, KeyValueRpcInterface stub) throws RemoteException {
    ClientLogger.info(String.format("Deleting pair with key %s ", key));
    KeyValueResponse res = stub.delete(key);
    if(res.isSuccess()) {
      ClientLogger.info("Pair deleted successfully");
    } else {
      ClientLogger.error(res.getErrorMsg());
    }
  }
}
