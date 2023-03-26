package org.phial.baas.manager.util;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.*;

/**
 * 网络工具
 */
public class NetUtils {


    private static String IPV4_PATTERN = "^((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))$";

    private NetUtils() {
    }

    private static Set<String> addresses;

    public static Set<String> getNetworkAddress() {
        if (addresses != null) return addresses;

        addresses = new HashSet<>();
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> iaddresses = ni.getInetAddresses();
                while(iaddresses.hasMoreElements()){
                    ip = iaddresses.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(':') == -1) {
                        addresses.add(ip.getHostAddress());
                    }
                }
            }
            return addresses;
        } catch (Exception e) {
        }

        return addresses;
    }

    public static String guessServerIp() {
        Set<String> adds = getNetworkAddress();
        if (adds != null) {
            for (String add : adds) {
                if (!"127.0.0.1".equals(add) && !"localhost".equalsIgnoreCase(add)) {
                    return add;
                }
            }
        }
        return "127.0.0.1";
    }

    public static boolean isLocal(String serverIp) {
        if ("localhost".equalsIgnoreCase(serverIp) || "127.0.0.1".equals(serverIp)) return true;
        return getNetworkAddress().contains(serverIp);
    }

    public static InetSocketAddress[] parseClusterAddresses(String clusterNodes[]) {
        if (clusterNodes != null && clusterNodes.length > 0) {

            List<InetSocketAddress> addresses = new ArrayList<>();

            for (String s : clusterNodes) {
                String ipAndPort[] = s.split(":");
                if (ipAndPort.length <= 1) throw new IllegalArgumentException("Incorrect ip address and port:" + s);

                String ip = ipAndPort[0];
                if (!isIpv4Address(ip)) throw new IllegalArgumentException("Incorrect ip address" + ip);

                try {
                    int port = Integer.parseInt(ipAndPort[1]);
                    if (port > 65535 || port < 0) new IllegalArgumentException("Incorrect network port:" + ipAndPort[1]);
                    addresses.add(new InetSocketAddress(ip, port));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Incorrect network port:" + ipAndPort[1]);
                }
            }

            InetSocketAddress [] as = new InetSocketAddress[clusterNodes.length];
            addresses.toArray(as);
            return as;
        }

        return null;
    }

    public static boolean isIpv4Address(String ip) {
        if (ip == null) return false;
        return ip.matches(IPV4_PATTERN);
    }
}