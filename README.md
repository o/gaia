#Gaia

Gaia is an open-source restful application for collecting and aggregating event metrics in timeseries manner. It shares same philosophy with [Simmetrica](https://github.com/simmetrica), but built for concurrency and scalability. Uses [Dropwizard](http://dropwizard.io) and [Redis](http://redis.io) under the hood.

###Prerequisites

* Java JDK 1.7
* Redis 2.6 or higher
* Maven (For building from source)

###Running

Get the latest source code from Github.

```
$ git clone https://github.com/o/gaia.git
```

For building a runnable "fat" JAR file, run package goal of maven in the root directory of project.

```
$ mvn package
```

After build process, JAR file will be created at `target` directory. You can run Gaia with the following command:

```
$ java -jar target/gaia-0.4.0.jar server config.yml
```

If you press `^C`, the application will shut down gracefully.

**You need to run `redis-server` before pushing events and querying stored data.**

###Configuration

Gaia uses Redis for storing data. If you need to configure default server and port address of Redis instance, you can configure it from `config.yml` file placed in root directory.

###Talking to Gaia

After running Gaia, it exposes restful api with JSON over HTTP for pushing and querying data. It can be accessible from `http://127.0.0.1:8000`.

####How to push events

For pushing a new event, we need to execute a HTTP POST request with JSON payload includes parameters. 

```
POST /events
{
  "name": "<String>",
  "increment": "Optional<Long>",
  "timestamp": "Optional<Long>"
}
```

**Required parameters**

`name`, which is canonical name of your input data. You will also use this name for querying event data. Event name must be consists with alphanumeric characters and dash. (Ex: `view-userprofile-890`)

**Optional parameters**

`increment`, this argument is useful for overriding event count for submitting multiple events in single operation. (Defaults to 1)

`timestamp`, if you need irregular updates, this argument lets you specify when event occurs. (Defaults to current Unix timestamp)

Simply, if you want to send only one event related to current time, specifying `name` parameter is good enough. If everything runs smoothly `201 Created` response with empty body is returned. If fails (Ex: connection interruption with Redis instance) `500 Internal Server Error` response is returned.

Example:

```
$ curl -i -X POST -H 'Content-Type: application/json' -d '{"name": "foo"}' http://localhost:8080/events

HTTP/1.1 201 Created
Date: Tue, 11 Nov 2014 14:21:12 GMT
Content-Type: application/json
Content-Length: 0
```

If parameters in JSON payload is could'nt be validated an error response with `400 Bad Request` for `increment` and `now` parameters, `422 Unprocessable Entity` for `event` parameter will be returned with error messages. If you forget to add `Content-Type: application/json` header in request `415 Unsupported Media Type` will be returned.


####How to query events

For querying events from Gaia, we simply execute an HTTP GET request and specify the parameters about event name, data granularity and time range. 

```
GET /events/<String>?start=<Long>&end=<Long>&resolution=<String>
```

**All parameters is mandatory**

`event`, as you guessed, we already used this value for feeding our data. 

`start` and `end` parameters take Unix timestamp for specifying interval of time-series.
 
`resolution` is used for defining the resolution / granularity of data. Possible values are `min`, `5min`, `15min`, `hour`, `day`, `week`, `month` and `year`.

Example:

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

###Events naming design

It's completely up to your **querying strategy**, keep it simple, short and meaningful. Don't hesitate to push multiple events for different querying requirements.

Here is the some examples:

Scenario: How many times a product page displayed?

Schema: `<group>-<object>-<identifier>`

Example: `view-product-324569`

---

Scenario: How many times a product page displayed from desktop **or** mobile?

Schema: `<group>-<object>-<identifier>-<tag>`

Example: `view-product-324569-desktop`, `view-product-324569-mobile`

---

Scenario: How many times a product page displayed from desktop **and / or** mobile?

Schema: `<group>-<object>-<identifier>-<tag>`

Example: `view-product-324569-desktop`, `view-product-324569-mobile`, `view-product-324569-total`

---

Scenario: How many times a product listed in search results?

Schema: `<group>-<object>-<identifier>`

Example: `listingview-product-324569`

###Internals

Gaia keeps event data in hashes. Hashes is very [memory efficient](http://instagram-engineering.tumblr.com/post/12202313862/storing-hundreds-of-millions-of-simple-key-value-pairs) and [plays well with CPU](http://redis.io/topics/memory-optimization). Also event key lookups and querying data is more faster.

All keys about events keeping under `gaia:` keyspace in redis. When you push a new event to Gaia, it send following commands to Redis.

```
"HINCRBY" "gaia:foo:min" "1415809680" "1"
"HINCRBY" "gaia:foo:15min" "1415808900" "1"
"HINCRBY" "gaia:foo:month" "1415232000" "1"
"HINCRBY" "gaia:foo:5min" "1415809500" "1"
"HINCRBY" "gaia:foo:year" "1387584000" "1"
"HINCRBY" "gaia:foo:day" "1415750400" "1"
"HINCRBY" "gaia:foo:hour" "1415808000" "1"
"HINCRBY" "gaia:foo:week" "1415232000" "1"
```

This operation is enclosed in a pipeline for sending multiple commands in a single step. Using pipelining hugely improves performance of push operation.

Gaia supports different resolutions from minute to year and keeps all values without expiring old values. So, you can query values of last 2 years in a minute precision. 

As of simplicity, Redis doesn't allow to expiring keys of hashes. Manual deletion of hash keys for clearing old values is very expensive operation if you have millions of events, but it's in the road map.

Gaia not supports data source types like `GAUGE` and `COUNTER`'s . Naturally it not uses fixed sized database like [RRDTool](http://oss.oetiker.ch/rrdtool/) or [Whisper](http://graphite.readthedocs.org/en/latest/whisper.html). 

###Deployment and operational tips

####Health checks

Thanks to Dropwizard, Gaia comes with built-in health check for verifying thread deadlocks and connectivity to Redis instance.

```
$ curl 'http://localhost:8081/healthcheck'

{"deadlocks":{"healthy":true},"jedis-pool":{"healthy":true}}
```

If all health checks report success, a `200 OK` is returned. If any fail, a `500 Internal Server Error` is returned with the error messages.

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

###Tests

You can run tests with `mvn test`.

###Contributing

Don't hesitate to make performance optimization proposals, adding new tests or helpful tricks to documentation. If you have any problems with running Gaia please open an issue from Issues page. 

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
