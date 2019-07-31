The health endpoint will return a simple 200 OK after a random delay, which one can setup the max value in milli using
the `delay` request query parameter.

    curl http://localhost:8081/health?delay=max_delay_in_milli

The default max delay is 1000 milliseconds.