
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

To integrate the TLS Grade Exporter into your Prometheus configuration,
configure it as a [Blackbox Exporter](https://github.com/prometheus/blackbox_exporter#prometheus-configuration). Add as many probe targets as
you want and direct Prometheus to your "real" TLS Grade Exporter endpoint
using relabeling:

```yaml
  - job_name: tls-grade-exporter
    metrics_path: /probe
    scrape_interval: 1h  # things do not change that often
    scrape_timeout: 3m   # cryptographic analysis takes time!
    static_configs:
      - targets:
        - https://prometheus.io    # 1st target to probe
        - https://ping7.io         # 2nd target to probe
    relabel_configs:
      - source_labels: [__address__]
        target_label: __param_target
      - source_labels: [__param_target]
        target_label: instance
      - target_label: __address__
        replacement: tls-grade-exporter:9218  # The tls-grade exporter's real hostname:port.

    # collect blackbox exporter's operational metrics
  - job_name: tls-grade-exporter-metrics
    static_configs:
      - targets:
        - tls-grade-exporter:9218
```

> Make sure to set the `scrape_timeout` to at least `3m`. Depending on compute
> resources and numbers of targets to probe in parallel, increase compute resources
> and/or `scrape_timeout`.

## Building

This Prometheus exporter is built using Java. Have a Java 17 (or later)
installed and Maven to build

```bash
mvn clean verify
```

To build the Docker image, use your local Docker installation

```bash
docker build -t tls-grade-exporter .
```
## License

[MIT](https://choosealicense.com/licenses/mit/)
