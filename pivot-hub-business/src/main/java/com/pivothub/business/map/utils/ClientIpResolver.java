package com.pivothub.business.map.utils;

import org.springframework.beans.factory.annotation.Autowired;

import com.pivothub.business.map.config.AmapProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import java.net.InetAddress;
import java.util.List;
import java.util.Optional;

/**
 * @Description: 客户端公网IPv4解析工具，根据可信代理配置解析转发链，用于网络城市识别
 * @Author lhb
 * @CreateTime 2026/10/5
 */
@Component
public class ClientIpResolver {
    @Autowired
    private AmapProperties properties;

    public Optional<String> publicIpv4(HttpServletRequest request) {
        String current = request.getRemoteAddr();
        if (literal(current) == null) return Optional.empty();
        if (trusted(current)) {
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                String[] chain = forwarded.split(",", -1);
                if (chain.length > 20) return Optional.empty();
                for (int index = chain.length - 1; index >= 0 && trusted(current); index--) {
                    current = chain[index].trim();
                    if (literal(current) == null) return Optional.empty();
                }
            }
        }
        if (trusted(current)) return Optional.empty();
        byte[] address = literal(current);
        if (address == null || address.length != 4 || !isPublic(address)) return Optional.empty();
        return Optional.of((address[0] & 255) + "." + (address[1] & 255) + "."
                + (address[2] & 255) + "." + (address[3] & 255));
    }

    private boolean trusted(String address) {
        byte[] candidate = literal(address);
        List<String> entries = properties.getProxy().getTrustedProxies();
        if (candidate == null || entries == null) return false;
        for (String entry : entries) {
            if (entry == null) continue;
            String[] parts = entry.trim().split("/", -1);
            if (parts.length > 2) continue;
            byte[] network = literal(parts[0]);
            if (network == null || network.length != candidate.length) continue;
            int bits;
            try { bits = parts.length == 1 ? network.length * 8 : Integer.parseInt(parts[1]); }
            catch (NumberFormatException invalid) { continue; }
            // A catch-all network never qualifies as a trusted proxy declaration.
            if (bits <= 0 || bits > network.length * 8) continue;
            boolean matches = true;
            for (int i = 0; i < network.length && bits > 0; i++) {
                int mask = (255 << (8 - Math.min(bits, 8))) & 255;
                if ((candidate[i] & mask) != (network[i] & mask)) { matches = false; break; }
                bits -= 8;
            }
            if (matches) return true;
        }
        return false;
    }

    private static byte[] literal(String value) {
        if (value == null || value.length() > 45) return null;
        if (value.matches("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+")) {
            String[] parts = value.split("\\.");
            byte[] result = new byte[4];
            for (int i = 0; i < 4; i++) {
                try {
                    if (parts[i].length() > 3 || (parts[i].length() > 1 && parts[i].startsWith("0"))) return null;
                    int number = Integer.parseInt(parts[i]);
                    if (number > 255) return null;
                    result[i] = (byte) number;
                } catch (NumberFormatException invalid) { return null; }
            }
            return result;
        }
        if (!value.contains(":") || !value.matches("[0-9a-fA-F:]+")) return null;
        try { return InetAddress.getByName(value).getAddress(); }
        catch (Exception invalid) { return null; }
    }

    private static boolean isPublic(byte[] address) {
        int a = address[0] & 255, b = address[1] & 255, c = address[2] & 255;
        return !(a == 0 || a == 10 || a == 127 || a >= 224
                || (a == 100 && b >= 64 && b <= 127)
                || (a == 169 && b == 254) || (a == 172 && b >= 16 && b <= 31)
                || (a == 192 && b == 168) || (a == 192 && b == 0 && (c == 0 || c == 2))
                || (a == 192 && b == 88 && c == 99)
                || (a == 198 && (b == 18 || b == 19)) || (a == 198 && b == 51 && c == 100)
                || (a == 203 && b == 0 && c == 113));
    }
}
