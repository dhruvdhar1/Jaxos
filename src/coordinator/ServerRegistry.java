package coordinator;

import java.util.ArrayList;
import java.util.List;

public class ServerRegistry {

  public static List<String> getRegisteredServerNames() {
    List<String> servers = new ArrayList<>();
    servers.add("rpc-server-4000");
    servers.add("rpc-server-4001");
    servers.add("rpc-server-4002");
    servers.add("rpc-server-4003");
    servers.add("rpc-server-4004");
    return servers;
  }
}
