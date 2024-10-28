FROM ghcr.io/observabilitystack/graalvm-maven-builder:22.0.1-ol9 AS builder

ADD . /build
WORKDIR /build

RUN mvn -B native:compile -P native --no-transfer-progress -DskipTests=true && \
    chmod +x /build/target/tls-grade-exporter

FROM oraclelinux:9-slim

RUN microdnf -y install util-linux procps dnsutils openssl

COPY --from=builder "/build/target/tls-grade-exporter" /tls-grade-exporter
COPY testssl.sh testssl.sh
CMD [ "/tls-grade-exporter" ]
