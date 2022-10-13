FROM eclipse-temurin:19-jre

RUN apt-get update && \
    apt-get -y install bsdmainutils dnsutils && \
    rm -rf /var/lib/apt/lists/*

COPY target/tls-grade-exporter-*.jar tls-grade-exporter.jar
COPY testssl.sh testssl.sh

ENTRYPOINT ["java","-jar","/tls-grade-exporter.jar"]
