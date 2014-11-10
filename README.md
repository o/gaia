```
     ██████╗  █████╗ ██╗ █████╗
    ██╔════╝ ██╔══██╗██║██╔══██╗
    ██║  ███╗███████║██║███████║
    ██║   ██║██╔══██║██║██╔══██║
    ╚██████╔╝██║  ██║██║██║  ██║
     ╚═════╝ ╚═╝  ╚═╝╚═╝╚═╝  ╚═╝
```

Restful application for collecting and aggregating event metrics in timeseries manner

Prequisities
============

* Java JDK 1.7
* Redis 2.6 or higher

Running
=======

```
mvn package
java -jar target/gaia-0.3.0.jar server config.yml 
```
