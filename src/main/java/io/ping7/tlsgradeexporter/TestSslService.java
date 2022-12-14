package io.ping7.tlsgradeexporter;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

/**
 * Runs the test ssl command
 */
@Service
@Log
@ImportRuntimeHints(TestSslService.TestSslServiceRuntimeHints.class)
public class TestSslService {

    private final ObjectMapper om = new ObjectMapper();

    public Mono<TlsGrade> rate(String target) {
        return Mono.fromFuture(CompletableFuture.supplyAsync(() -> computeTestSslOutput(target)))
                .onErrorComplete()
                .map(s -> toTlsGrade(s, target));
    }

    private String computeTestSslOutput(String target) {

        String rating = null;

        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(rateCommandFor(target));
            Process process = builder.start();

            // wait for testssl to finish
            process.waitFor();

            // the JSON result is printed to STDERR
            rating = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warning(String.format("Could not execute testssl.sh: %s", e.getMessage()));
            throw new RuntimeException(e);
        }

        return rating;
    }

    private TlsGrade toTlsGrade(String output, String target) {
        final TlsGrade grade = new TlsGrade();
        grade.setTarget(target);

        // get all findings from the current run
        try {
            final List<TestSslFinding> findings = om.readValue(output, new TypeReference<List<TestSslFinding>>() {
            });

            for (TestSslFinding f : findings) {
                // protocol_support_score
                if ("protocol_support_score".equals(f.getId())) {
                    grade.setProtocolSupportScore(Integer.valueOf(f.getFinding()));
                }
                // key_exchange_score
                else if ("key_exchange_score".equals(f.getId())) {
                    grade.setKeyExchangeScore(Integer.valueOf(f.getFinding()));
                }
                // cipher_strength_score
                else if ("cipher_strength_score".equals(f.getId())) {
                    grade.setCipherStrengthScore(Integer.valueOf(f.getFinding()));
                }
                // final_score
                else if ("final_score".equals(f.getId())) {
                    grade.setFinalScore(Integer.valueOf(f.getFinding()));
                }
                // overall_grade
                else if ("overall_grade".equals(f.getId())) {
                    grade.setGrade(f.getFinding());
                }
                // scanTime
                else if ("scanTime".equals(f.getId())) {
                    grade.setScanTime(Integer.valueOf(f.getFinding()));
                    grade.setTarget(f.getIp());
                }
            }
        } catch (Exception e) {
            log.warning(String.format("Could not convert testssl result into final grade: %s", e.getMessage()));
            throw new RuntimeException(e);
        }

        return grade;
    }

    private String[] rateCommandFor(String target) {
        return new String[] { "testssl.sh/testssl.sh", "--quiet", "--color", "0", "--jsonfile",
                "/dev/stderr", "--quiet", "--hints", target };
    }

    static class TestSslServiceRuntimeHints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection()
                    .registerType(TestSslFinding.class,
                            MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                            MemberCategory.INVOKE_PUBLIC_METHODS);
        }
    }

}
