package server;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Singleton class to store key-value pairs using a hashmap.
 */
public class KeyValue {

  private static KeyValue singletonInstance;
  private final HashMap <String, String> keyValStore;
  private final ReentrantLock mutex;

  private KeyValue() {
    keyValStore = new HashMap();
    mutex = new ReentrantLock();
    singletonInstance = null;
  }

  /**
   * Returns instance of this class. This class is instanced only once. This method returns
   * previously created object if the class was already instantiated.
   * @return instance of the class.
   */
  public static KeyValue getInstance() {
    if(singletonInstance == null) {
      singletonInstance = new KeyValue();
    }
    return singletonInstance;
  }

  /**
   * Adds a key-value pair to the store. If is already present, then the value is updated. Method
   * is thread-safe and can be accessed by only one thread at a time.
   * @param key key to be inserted.
   * @param value value pertaining to the key.
   */
  public void put(String key, String value) {
    try {
      mutex.lock();
      keyValStore.put(key, value);
      ServerLogger.info("Added pair " + key + " : " + value + " to hashmap");
    } finally {
      mutex.unlock();
    }
  }

  /**
   * Deletes an entry form the key-value store. Does nothing is the key is not present
   * in the hashmap. Method is thread-safe and can be accessed by only one thread at a time.
   * @param key key pertaining to the entry to be deleted.
   * @return deletion status.
   */
  public boolean delete(String key) {
    try {
      mutex.lock();
      if(keyValStore.containsKey(key)) {
        ServerLogger.info("Deleted key: "+ key + " from hashmap");
        keyValStore.remove(key);
        return true;
      }
      ServerLogger.error(String.format("key %s not found in hashmap", key));
      return false;
    } finally {
      mutex.unlock();
    }
  }

  /**
   * Retrieves an entry form the key-value store. Returns null is the key is not present
   * in the hashmap.
   * @param key key pertaining to the value to be returned.
   * @return value.
   */
  public String get(String key) {
    try {
      mutex.lock();
      if (keyValStore.containsKey(key)) {
        String value = keyValStore.get(key);
        ServerLogger.info("Returned value: " + value + " for key: " + key);
        return value;
      }
      ServerLogger.error(String.format("key %s not found in hashmap", key));
      return null;
    } finally {
      mutex.unlock();
    }
  }
}
