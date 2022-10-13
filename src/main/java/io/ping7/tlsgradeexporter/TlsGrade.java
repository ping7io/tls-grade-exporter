package io.ping7.tlsgradeexporter;

import lombok.Data;

@Data
public class TlsGrade {

    private String target;
    private int protocolSupportScore;
    private int keyExchangeScore;
    private int cipherStrengthScore;
    private int finalScore;
    private String grade;
    private int scanTime;

    public boolean isValid() {
        return grade != null && finalScore > 0;
    }

}
