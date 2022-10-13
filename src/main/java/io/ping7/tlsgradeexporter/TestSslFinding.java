package io.ping7.tlsgradeexporter;

import lombok.Data;

@Data
public class TestSslFinding {

    private String id;
    private String ip;
    private String port;
    private String cwe;
    private String cve;
    private String severity;
    private String finding;
}
