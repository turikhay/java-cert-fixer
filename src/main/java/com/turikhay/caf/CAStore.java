package com.turikhay.caf;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.stream.Collectors;

public class CAStore {

    public static CAStore load(InputStream input, String type, String password) throws Exception {
        final KeyStore keyStore = KeyStore.getInstance(type);
        try {
            keyStore.load(input, password.toCharArray());
        } finally {
            input.close();
        }
        return new CAStore(keyStoreToCertSet(keyStore));
    }

    private static Set<Cert> keyStoreToCertSet(KeyStore keyStore) throws KeyStoreException {
        Map<Certificate, Set<String>> certAliasMap = new HashMap<>();
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            Certificate certificate = keyStore.getCertificate(alias);
            certAliasMap.compute(certificate, (__, set) -> {
                if (set == null) {
                    set = new HashSet<>();
                }
                set.add(alias);
                return set;
            });
        }
        return certAliasMap.entrySet().stream().map(entry ->
                new Cert(entry.getKey(), entry.getValue())
        ).collect(Collectors.toSet());
    }

    private final Set<Cert> certs;

    private CAStore(Set<Cert> certs) {
        this.certs = certs;
    }

    public boolean hasCert(Cert cert) {
        return certs.contains(cert);
    }

    public Set<Cert> getCerts() {
        return certs;
    }

    public CAStore merge(CAStore anotherStore) {
        Set<Cert> result = newCertSet();
        result.addAll(this.certs);
        result.addAll(anotherStore.certs);
        return new CAStore(result);
    }

    public KeyStore toKeyStore() throws KeyStoreException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyStore.load(null, new char[0]);
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new KeyStoreException("Couldn't init empty KeyStore", e);
        }
        for (Cert cert : certs) {
            for (String alias : cert.getAliases()) {
                keyStore.setCertificateEntry(alias, cert.getCertificate());
            }
        }
        return keyStore;
    }

    private static Set<Cert> newCertSet() {
        return new HashSet<>();
    }
}
