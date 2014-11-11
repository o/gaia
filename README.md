#Gaia

Gaia is an open-source restful application for collecting and aggregating event metrics in timeseries manner. It shares same philosophy with [Simmetrica](https://github.com/simmetrica), but built for concurrency and scalability. Uses [Dropwizard](http://dropwizard.io) and [Redis](http://redis.io) under the hood.

###Prerequisites

* Java JDK 1.7
* Redis 2.6 or higher
* Maven (For building from source)

###Running

For building a runnable fat jar file, you need to run package goal of maven.

```
$ mvn package
```

After build process, jar file will be created at `target` directory.

```
$ java -jar target/gaia-0.3.0.jar server config.yml
```

*You need to run `redis-server` before pushing events and querying stored data.*

###Configuration

Gaia uses redis for storing data. If you need to configure default server and port address of redis instance, you can configure it from `config.yml` file placed in root directory.

###Talking to Gaia

After running Gaia, it exposes restful api with JSON over HTTP for pushing and querying data. It can be accessible from `http://127.0.0.1:8000`.

####How to push events

TODO

```
$ curl -i -X POST -H 'Content-Type: application/json' -d '{"name": "foo"}' http://localhost:8080/events

HTTP/1.1 201 Created
Date: Tue, 11 Nov 2014 14:21:12 GMT
Content-Type: application/json
Content-Length: 0
```

####How to query events

TODO

```
$ curl -sS -X GET 'http://localhost:8080/events/foo?start=1415697030&end=1415718630&resolution=hour'

[
    {
        "count": 31356,
        "timestamp": 1415696400
    },
    {
        "count": 28763,
        "timestamp": 1415700000
    },
    {
        "count": 38561,
        "timestamp": 1415703600
    },
    {
        "count": 44677,
        "timestamp": 1415707200
    },
    {
        "count": 54810,
        "timestamp": 1415710800
    },
    {
        "count": 52345,
        "timestamp": 1415714400
    },
    {
        "count": 49780,
        "timestamp": 1415718000
    }
]

```

###Events

TODO

###Tips and tricks

####Running with supervisord

TODO

####Running behind load balancer

TODO

###Internals

TODO

###Tests

You can run tests with `mvn test`.

###Contributing

TODO 

###License

Copyright (c) 2014 Osman Ungur

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is furnished
to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

