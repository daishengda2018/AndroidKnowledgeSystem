package com.example.dsd.demo.network.okhttp;

import com.example.dsd.demo.BuildConfig;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * 用户Https双向认证
 * Created by im_dsd on 2019-06-14
 */
public class HttpsUtils {

    private HttpsUtils() {
    }

    public static HttpsUtils getInstance() {
        return SingletonInstance.INSTANCE;
    }

    private static class SingletonInstance {
        private static final HttpsUtils INSTANCE = new HttpsUtils();
    }


    /**
     * 生成SSL参数
     * @param certificateFile 证书文件
     * @param bksFile 密钥库文件
     * @param password 密钥库密码
     */
    public SslParam generateSslParam(InputStream certificateFile, InputStream bksFile, String password) {
        SslParam sslParam = new SslParam();
        try {
            TrustManager[] trustManagers;
            // debug模式下信任所有证书
            if (BuildConfig.DEBUG) {
                trustManagers = new TrustManager[]{new UnSafeTrustManager()};
            } else {
                //release下只信任自定义证书
                trustManagers = generateTrustManager(certificateFile);
            }
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyManager[] keyManagers = null;
            if (bksFile != null) {
              keyManagers = generateKeyManagers(bksFile, password);
            }
            sslContext.init(keyManagers, trustManagers, new SecureRandom());
            if (trustManagers == null || trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
            }
            sslParam.socketFactory = sslContext.getSocketFactory();
            sslParam.trustManagers = (X509TrustManager) trustManagers[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslParam;
    }

    /**
     * 生成Trust Manager
     * @param certificateFile 证书文件
     */
    private TrustManager[] generateTrustManager(InputStream certificateFile) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(certificateFile);
            // KeyStore相当于一个数据库，用于存储证书的
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            //初始化
            keyStore.load(null);
            int index = 0;
            for (Certificate certificate : certificates) {
                // 遍历证书存储
                keyStore.setCertificateEntry(String.valueOf(++index), certificate);
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            return trustManagerFactory.getTrustManagers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 生成Key Managers
     * @param bksFile 本地密钥库文件
     * @param password 密钥库文件密码
     * @return
     */
    private KeyManager[] generateKeyManagers(InputStream bksFile, String password) {
       try {
           KeyManagerFactory managerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
           KeyStore keyStore = KeyStore.getInstance("BKS");
           keyStore.load(bksFile, password.toCharArray());
           managerFactory.init(keyStore, password.toCharArray());
           return managerFactory.getKeyManagers();
       } catch (Exception e) {
           e.printStackTrace();
       }
        return null;
    }

    public static class UnSafeTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    /**
     * ssl参数类
     */
    public static class SslParam {
        public X509TrustManager trustManagers;
        public SSLSocketFactory socketFactory;
    }
}
