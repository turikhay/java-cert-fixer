package com.turikhay.cja;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;

public class KeyStoreManager {

    public static void useNewKeyStore(KeyStore keyStore) throws Exception {
        TrustManagerFactory instance = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        instance.init(keyStore);
        final SSLContext tls = SSLContext.getInstance("TLS");
        tls.init(null, instance.getTrustManagers(), null);
        HttpsURLConnection.setDefaultSSLSocketFactory(tls.getSocketFactory());
    }

    private KeyStoreManager() {
    }

}
