package com.example.dragos.userdatamonitor;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * This class will contain the code that will be used by the ReadFromWhiti
 * to allow it to trust ALL certificates (including our own currently self-signed one)
 * so that the connection to our HTTPS server will be successful
 *
 * TODO: This class should be removed if the current https server
 * TODO: has a certificate that is signed by a trusted third party source (i.e Externally signed)
 *
 * Created by Dragos on 9/30/17.
 */


public class TrustAllCertifcates {

    // TODO:  for trusting the self signed certificate of our HTTPS server:
    // TODO : change name and research more on the behavior of this code

    // Verifier that verifies all hosts:
    private static final HostnameVerifier DUMMY_VERIFIER = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Create a trust manager that trusts all certficates, including a self signed one
     * otherwise the connection will not be succssful in the AsyncTask
     */
    public void trustAllCertificates() {

        // Dummy trust manager that trusts all certificates
        TrustManager localTrustmanager = new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {}

            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {}
        };

        // Create SSLContext and set the socket factory as default
        try {
            SSLContext sslc = SSLContext.getInstance("TLS");
            sslc.init(null, new TrustManager[] { localTrustmanager }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
        }
        catch (Exception e) {e.printStackTrace();}
    }


    public HostnameVerifier getDummyVerifier () {
        return DUMMY_VERIFIER;
    }
}
