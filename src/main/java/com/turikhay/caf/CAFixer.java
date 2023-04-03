package com.turikhay.caf;

import com.turikhay.caf.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.lang.instrument.Instrumentation;
import java.security.KeyStore;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;

import static com.turikhay.caf.CAStore.load;

/**
 * Zero-dependency utility that adds ISRG Root X1 certificate authority for older Java 8 versions (before Update 101)
 */
public class CAFixer {

    /**
     * Runs the utility. Typically called by VM if CAFixer is used as a Java agent.
     * 
     * @param args args
     * @param inst instrumentation
     */
    public static void premain(String args, Instrumentation inst) {
        fix();
    }

    /**
     * Runs the utility and exits
     * 
     * @param args args
     * 
     * @see #fix()
     */
    public static void main(String[] args) {
        fix();
    }

    /**
     * Runs the utility with specified Logger
     * @param logger custom logger implementation; will print to stdout if {@code null}
     */
    public static void fix(Logger logger) {
        new CAFixer(logger == null ? Logger.PrintLogger.ofSystem() : logger).fixCA();
    }

    /**
     * Runs the utility. Will print logs to stdout.
     */
    public static void fix() {
        fix(null);
    }

    private final Logger logger;

    private CAFixer(Logger logger) {
        this.logger = logger;
    }

    private void fixCA() {
        try {
            updateJreCAStoreIfNecessary();
        } catch (Exception e) {
            logger.logError(LOG_PREFIX + "Failed", e);
        }
    }

    private void updateJreCAStoreIfNecessary() throws Exception {
        CAStore jreCAStore = loadJreCAStore();
        CAStore embeddedCAStore = loadEmbeddedCAStore();
        if (doesContainAllCerts(jreCAStore, embeddedCAStore)) {
            return;
        }
        CAStore mergedCAStore = jreCAStore.merge(embeddedCAStore);
        KeyStore mergedKeyStore = mergedCAStore.toKeyStore();
        log("Will use updated KeyStore that includes missing certificates");
        KeyStoreManager.useNewKeyStore(mergedKeyStore);
    }

    private boolean doesContainAllCerts(CAStore jreCAStore, CAStore embeddedCAStore) {
        boolean allSet = true;
        for (Cert cert : embeddedCAStore.getCerts()) {
            boolean shouldSkip = cert.asX509().map(x509 -> {
                try {
                    x509.checkValidity();
                } catch (CertificateExpiredException e) {
                    log("Embedded certificate has expired " + cert);
                    return Boolean.TRUE;
                } catch (CertificateNotYetValidException e) {
                    log("Embedded certificate is not yet valid..? " + cert);
                }
                return Boolean.FALSE;
            }).filter(b -> b == Boolean.TRUE).isPresent();
            if (shouldSkip) {
                log("Skipping " + cert);
                continue;
            }
            if (!jreCAStore.hasCert(cert)) {
                log("JRE trust store doesn't contain " + cert);
                allSet = false;
            }
        }
        return allSet;
    }

    private void log(String message) {
        logger.logMessage(LOG_PREFIX + message);
    }

    private static final String LOG_PREFIX = "[CAFixer] ";

    private static CAStore loadJreCAStore() throws Exception {
        File cacertsFile = new File(System.getProperty("java.home"), "lib/security/cacerts");
        return load(new FileInputStream(cacertsFile), KeyStore.getDefaultType(), "changeit");
    }

    private static CAStore loadEmbeddedCAStore() throws Exception {
        return load(CAFixer.class.getResourceAsStream("ca.jks"), "jks", "supersecretpassword");
    }
}
