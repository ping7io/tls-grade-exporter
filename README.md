
# Prometheus TLS Grade Exporter

A Prometheus Exporter that rates HTTPS endpoints according to the 
[SSLLabs Server Rating Guide](https://github.com/ssllabs/research/wiki/SSL-Server-Rating-Guide)


## Running

The easiest way to run this exporter uses the Docker image

```bash
docker run -p 9218:9218 ghcr.io/ping7io/tls-grade-exporter:latest
```

To retrieve TLS grades for a given website (`ping7.io` in this case),
use the `/probe` endpoint

```bash
curl "http://localhost:9218/probe?target=ping7.io"
[...]
```



## Integrating into Prometheus

TBD
## Building

This Prometheus exporter is built using Java. Have a Java 17 (or later) 
installed and Maven to build

```bash
mvn clean verify
```

To build the Docker image afterwards, use your local Docker installation

```bash
docker build -t tls-grade-exporter .
```
## License

[MIT](https://choosealicense.com/licenses/mit/)

