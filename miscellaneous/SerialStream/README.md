# Serial Port Stream Sample


This sample show how to use escpos-coffee with serial port stream.

It uses the jSerialComm dependency


https://fazecast.github.io/jSerialComm/

https://github.com/Fazecast/jSerialComm



##### 1. Edit Information about the port to be used on Principal.java
```java
    public static void main(String[] args) throws IOException {
        Principal obj = new Principal();
        obj.fnc1("com1"); // CHANGE HERE

```

##### 2. Compile and run
```shell script
mvn clean package
java -jar java -jar target\SerialStream-4.0.1-jar-with-dependencies.jar
```


