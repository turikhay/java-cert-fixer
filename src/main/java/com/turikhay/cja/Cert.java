package com.turikhay.cja;

import javax.security.auth.x500.X500Principal;
import java.security.cert.*;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Cert {
    private final Certificate certificate;
    private final Set<String> aliases;

    private DualFingerprint fingerprint;
    private String subject;

    public Certificate getCertificate() {
        return certificate;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public Optional<X509Certificate> asX509() {
        return certificate instanceof X509Certificate ? Optional.of((X509Certificate) certificate) : Optional.empty();
    }

    public Cert(Certificate certificate, Set<String> aliases) {
        this.certificate = certificate;
        this.aliases = aliases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cert cert = (Cert) o;
        return certificate.equals(cert.certificate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificate);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "Cert{%s,%s}", getSubject(), getFingerprintFormatted());
    }

    private DualFingerprint getFingerprint() throws CertificateEncodingException {
        if (fingerprint == null) {
            fingerprint = computeFingerprint(certificate);
        }
        return fingerprint;
    }

    private String getFingerprintFormatted() {
        DualFingerprint fingerprint;
        try {
            fingerprint = getFingerprint();
        } catch (CertificateEncodingException e) {
            return e.toString();
        }
        return String.format(Locale.ROOT, "SHA-1:%s,SHA-256:%s", fingerprint.getSha1(), fingerprint.getSha256());
    }

    private String getSubject() {
        if (subject == null) {
            subject = extractSubject(certificate);
        }
        return subject;
    }

    private static DualFingerprint computeFingerprint(Certificate cert) throws CertificateEncodingException {
        return DualFingerprint.compute(cert.getEncoded());
    }

    private static String extractSubject(Certificate cert) {
        if (cert instanceof X509Certificate) {
            X509Certificate x509 = (X509Certificate) cert;
            X500Principal principal = x509.getSubjectX500Principal();
            if (principal != null) {
                String name = principal.getName();
                if (name != null) {
                    return name;
                }
            }
        }
        return "";
    }
}
