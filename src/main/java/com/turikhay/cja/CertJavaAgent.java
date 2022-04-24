package com.turikhay.cja;

import java.io.File;
import java.io.FileInputStream;
import java.lang.instrument.Instrumentation;
import java.security.KeyStore;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;

import static com.turikhay.cja.CAStore.load;

public class CertJavaAgent {

    public static void premain(String args, Instrumentation inst) {
        try {
            updateJreCAStoreIfNecessary();
        } catch (Exception e) {
            System.err.println(LOG_PREFIX + "Failed");
            e.printStackTrace();
        }
    }

    private static void updateJreCAStoreIfNecessary() throws Exception {
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

    private static CAStore loadJreCAStore() throws Exception {
        File cacertsFile = new File(System.getProperty("java.home"), "lib/security/cacerts");
        return load(new FileInputStream(cacertsFile), KeyStore.getDefaultType(), "changeit");
    }

    private static CAStore loadEmbeddedCAStore() throws Exception {
        return load(CertJavaAgent.class.getResourceAsStream("ca.jks"), "jks", "supersecretpassword");
    }

    private static boolean doesContainAllCerts(CAStore jreCAStore, CAStore embeddedCAStore) {
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

    private static void log(String message) {
        System.out.println(LOG_PREFIX + message);
    }

    private static final String LOG_PREFIX = "[CertJavaAgent] ";
}
