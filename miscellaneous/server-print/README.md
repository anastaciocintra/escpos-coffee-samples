#server-print
This is an example code to pipe stream between tcp/ip connection and local printer. 
That is, it receives tcp/ip stream and send to the local printer.

This code exist to provide a tool to run the android sample app tcpip-stream

[AndroidImage](../AndroidImage) and [tcpip-stream](../../usual/tcpip-stream)


But you can use this for alternative tool for share one printer over clients in your local network.


running:
 ```shell script
mvn package
java -jar target/server-print[version]-jar-with-dependencies.jar tm-t20 9100 

``` 
