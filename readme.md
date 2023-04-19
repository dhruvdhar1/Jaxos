## Project4: Paxos Implementation using RPC
This client-server application is used to store key-value pairs on the server. The client makes an RPC
request to the server with request type and request data and the server executes the request.
The server supports 3 types of operation:\

PUT: This is used to store key-value pairs on the server.\
GET: This is used to fetch values pertaining to a particular key from the server.\
DELETE: This is used to delete an entry pertaining to a key from the server.\


In this setup, the application is composed of 3 parts, the client, coordinator and the server
applications. The coordinator is used to orchestrate the paxos algorithm. The client makes a request
to the server, which generates a proposal and sends it to the coordinator. The coordinator upon 
receiving the proposal initiates Paxos. It first sends a 'prepare' message to all the acceptors. If
majority acceptors acknowledge the proposal, then the coordinator moves on to 'accept' stage. If
the majority of the acceptors accept the proposal, then the coordinator sends a 'learn' message to 
the learners. The learners upon receiving the learn request execute the operation on the key-value 
store. Paxos is run for DELETE and PUT operations only.

Please note that for this setup, the 5 servers need to run on:
- 127.0.0.1:4000
- 127.0.0.1:4001
- 127.0.0.1:4002
- 127.0.0.1:4003
- 127.0.0.1:4004

To run this setup, follow the following steps:
1. start rmiregistry in a terminal.
2. Run 5 servers on five different terminals. These servers should run on ports 4000,4001,4002,4003,4004.
3. In a different terminal, run the coordinator application.
4. In a different terminal, run the client application.

Sometimes rmiregistry takes some time to start. If you get a connection refused exception on server 
start, wait for some time and then restart the server. If everything works, you should see a "Server
ready!" message on the console.

The client interface looks like:
```java
1. Get
2. Put
3. Delete
4. Exit
```
The user needs to enter an integer corresponding to the option to select an operation (for example
2 for a 'Put' operation).


To run the server, execute the following command on the Server jar file:
```java
java -cp server.jar server.ServerApp <port-number>
eg: java -cp server.jar server.ServerApp 4000
```

As mentioned before, please run the 5 servers on:
- 127.0.0.1:4000
- 127.0.0.1:4001
- 127.0.0.1:4002
- 127.0.0.1:4003
- 127.0.0.1:4004

This is important as the coordinator is pre-configured to use these hosts to connect with the server.

To run the coordinator, execute the following command on the Coordinator jar file:
```java
java -cp coordinator.jar coordinator.CoordinatorApp
eg: java -cp coordinator.jar coordinator.CoordinatorApp
```

To run the client, execute the following command on the client jar file
```java
java -cp client.jar client.ClientApp <server-host> <server-port-number>
eg: java -cp client.jar client.ClientApp 127.0.0.1 4000
```
#### NOTE Please pass 4000 as server-port-number when running the client application.

To build the project, no special steps are required. Simply import the project in intelliJ and build.

On running the client, the client first executes a set of hardcoded PUT requests to pre-populate the server
and then executes 5 GET and 5 DELETE requests as required (5 PUT already executed before).
After that user will get a prompt to Enter request.

#### NOTE: The proposers and acceptors have been rigged to fail at a rate pf 10%. This is done to emulate real-life failures.

#### NOTE: Every 'prepare' request is artificially delayed by a random interval(>=0 && < 1s). This is done to emulate real-life network delays.

The response may contain status codes which are described below:

##Response status codes
200: OK\
404: Not found

