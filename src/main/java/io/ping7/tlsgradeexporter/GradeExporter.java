package io.ping7.tlsgradeexporter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class GradeExporter {

    @Autowired
    private TestSslService testSsl;

    @GetMapping("/metrics")
    public Mono<ResponseEntity<String>> tlsGradeMetrics(@RequestParam String target) {
        return testSsl.rate(target)
                .map(this::toPrometheusResponse);
    }

    ResponseEntity<String> toPrometheusResponse(TlsGrade grade) {
        final StringBuilder body = new StringBuilder();

        if (!grade.isValid()) {
            // tls_grade_info
            body.append(String.format("tls_grade_info{target=\"%s\"} 0", grade.getTarget()));
            body.append('\n');

        } else {

            // tls_grade_info
            body.append(String.format("tls_grade_info{target=\"%s\", scan_time=\"%s\", grade=\"%s\"} 1",
                    grade.getTarget(), grade.getScanTime(), grade.getGrade()));
            body.append('\n');

            // tls_grade_score_overall
            body.append(createPromtheusMetricsFor("tls_grade_score_overall", grade, grade.getFinalScore()));
            body.append('\n');

            // tls_grade_score_cipher_strength
            body.append(
                    createPromtheusMetricsFor("tls_grade_score_cipher_strength", grade,
                            grade.getCipherStrengthScore()));
            body.append('\n');

            // tls_grade_score_key_exchange
            body.append(createPromtheusMetricsFor("tls_grade_score_key_exchange", grade, grade.getKeyExchangeScore()));
            body.append('\n');

            // tls_grade_score_protocol_support
            body.append(
                    createPromtheusMetricsFor("tls_grade_score_protocol_support", grade,
                            grade.getProtocolSupportScore()));
            body.append('\n');
        }

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(body.toString());
    }

    private String createPromtheusMetricsFor(String metricName, TlsGrade grade, int value) {
        return String.format("%s{target=\"%s\", grade=\"%s\"} %s",
                metricName, grade.getTarget(), grade.getGrade(), value);
    }

}
