#Gaia

Gaia is an open-source restful application for collecting and aggregating event metrics in timeseries manner. It shares same philosophy with [Simmetrica](https://github.com/simmetrica), but built for concurrency and scalability. Uses [Dropwizard](http://dropwizard.io) and [Redis](http://redis.io) under the hood.

###Prerequisites

* Java JDK 1.7
* Redis 2.6 or higher
* Maven (For building from source)

###Running

Get the latest source code from Github.

```
git clone https://github.com/o/gaia.git
```

For building a runnable "fat" JAR file, run package goal of maven in the root directory of project.

```
$ mvn package
```

After build process, JAR file will be created at `target` directory. You can run Gaia with the following command:

```
$ java -jar target/gaia-0.3.0.jar server config.yml
```

If you press `^C`, the application will shut down gracefully.

**You need to run `redis-server` before pushing events and querying stored data.**

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

###Data storage model

TODO

###Deployment and operational tips

####Health checks

Thanks to Dropwizard, Gaia comes with built-in health check for verifying connectivity to redis instance and application deadlocks.

```
$ curl 'http://localhost:8081/healthcheck'

{"deadlocks":{"healthy":true},"jedis-pool":{"healthy":true}}
```

If all health checks report success, a 200 OK is returned. If any fail, a 500 Internal Server Error is returned with the error messages.

####Running behind Nginx and Haproxy

Here is the example Nginx configuration:

```
server {
    listen 80;

    server_name metrics.domain.com;

    location / {
        proxy_pass http://localhost:8080;
    }

    location /ping {
        proxy_pass http://localhost:8081;
        root /ping;
    }

}
```

To deny access to 8080 and 8081 (used for administrative purposes) ports run following rules:
 
```
iptables -A INPUT -p tcp -i eth0 --dport 8080 -j REJECT --reject-with tcp-reset
iptables -A INPUT -p tcp -i eth0 --dport 8081 -j REJECT --reject-with tcp-reset
```

If you're running several Gaia instances behind Haproxy, you can enable health checks with the configuration like this:

```
frontend http
    bind :80
    
    ...
    default_backend gaia_cluster

backend gaia_cluster
    option httpchk GET /ping
    http-check expect string pong

    ...
    server metrics04 metrics04.domain.com:80 check
```

####Running with supervisord

[Supervisor](http://supervisord.org/) allows users to monitor and control a number of processes on UNIX-like operating systems. Installation instructions can be found in [here](http://supervisord.org/installing.html#installing-a-distribution-package).

To running Gaia as a daemon with supervisord, add the following lines to supervisord configuration.

```
[program:gaia]
command=/usr/bin/java -jar /path/to/gaia.jar server /path/to/config.yml
```

Then run following commands:

```
supervisorctl reread
supervisorctl update
```

For checking status of Gaia daemon, run `supervisorctl status`.

```
gaia                             RUNNING    pid 10636, uptime 39 days, 6:15:15
```

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
