FROM ghcr.io/graalvm/graalvm-ce:ol9-java17-22.3.2 AS builder

ADD . /build
WORKDIR /build

# For SDKMAN to work we need unzip & zip
RUN microdnf -y install zip

RUN \
    # Install SDKMAN
    curl -s "https://get.sdkman.io" | bash && \
    source "$HOME/.sdkman/bin/sdkman-init.sh" && \
    sdk install maven && \
    gu install native-image

RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && \
    mvn --version && \
    native-image --version

RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && \
    mvn -B native:compile -P native --no-transfer-progress -DskipTests=true && \
    chmod +x /build/target/tls-grade-exporter


FROM oraclelinux:9-slim

RUN microdnf -y install util-linux procps dnsutils openssl

COPY --from=builder "/build/target/tls-grade-exporter" /tls-grade-exporter
COPY testssl.sh testssl.sh
CMD [ "/tls-grade-exporter" ]
