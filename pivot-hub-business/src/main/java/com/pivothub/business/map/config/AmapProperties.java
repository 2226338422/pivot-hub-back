package com.pivothub.business.map.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "amap")
public class AmapProperties {
    private Js js = new Js();
    private Web web = new Web();
    private Proxy proxy = new Proxy();

    public Js getJs() { return js; }
    public void setJs(Js js) { this.js = js; }
    public Web getWeb() { return web; }
    public void setWeb(Web web) { this.web = web; }
    public Proxy getProxy() { return proxy; }
    public void setProxy(Proxy proxy) { this.proxy = proxy; }

    public static class Js {
        private String key;
        private String securityJsCode;
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getSecurityJsCode() { return securityJsCode; }
        public void setSecurityJsCode(String securityJsCode) { this.securityJsCode = securityJsCode; }
    }

    public static class Web {
        private String key;
        private String privateKey;
        private boolean signatureEnabled = true;
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getPrivateKey() { return privateKey; }
        public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }
        public boolean isSignatureEnabled() { return signatureEnabled; }
        public void setSignatureEnabled(boolean signatureEnabled) { this.signatureEnabled = signatureEnabled; }
    }

    public static class Proxy {
        private String publicPath;
        private List<String> allowedOrigins;
        private List<String> trustedProxies = new ArrayList<>();
        private int requestsPerMinute = 120;
        public String getPublicPath() { return publicPath; }
        public void setPublicPath(String publicPath) { this.publicPath = publicPath; }
        public List<String> getAllowedOrigins() { return allowedOrigins; }
        public void setAllowedOrigins(List<String> allowedOrigins) { this.allowedOrigins = allowedOrigins; }
        public List<String> getTrustedProxies() { return trustedProxies; }
        public void setTrustedProxies(List<String> trustedProxies) { this.trustedProxies = trustedProxies; }
        public int getRequestsPerMinute() { return requestsPerMinute; }
        public void setRequestsPerMinute(int requestsPerMinute) { this.requestsPerMinute = requestsPerMinute; }
    }
}
