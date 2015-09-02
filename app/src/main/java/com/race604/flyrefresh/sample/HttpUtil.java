package com.race604.flyrefresh.sample;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpUtil {
    public static HttpClient httpClient = new DefaultHttpClient();
    public static String getRequest(final String url) throws Exception {

        FutureTask<String> task = new FutureTask<String>(
                new Callable<String>() {

                    @Override
                    public String call() throws Exception {

                        HttpGet get = new HttpGet(url);
                        HttpResponse httpResponse = httpClient.execute(get);
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {

                            String result = EntityUtils.toString(httpResponse
                                    .getEntity());
                            return result;
                        }
                        return null;

                    }

                });

        new Thread(task).start();
        return task.get();

    }


    /**
     * @return 杩斿洖鎵嬫鐨処P鍦板潃锛屽湪Oauth鐧诲綍鏃堕渶瑕佸～鍐欑殑redirect_uri鐨勫��
     */
    public static String getPsdnIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

   
}
