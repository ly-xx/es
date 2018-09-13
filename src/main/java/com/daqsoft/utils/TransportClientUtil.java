package com.daqsoft.utils;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.context.annotation.Bean;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 获取传输对象
 *
 * @author liaoxiaoxia
 * @version 1.0.0
 * @date 2018-9-13 16:32
 * @since JDK 1.8
 */
public class TransportClientUtil {

    private static TransportClient client;

    static {
        Settings settings = Settings.builder()
                // 集群名称
                .put("cluster.name", "l_elasticsearch")
                // 开启集群探测功能
                .put("client.transport.sniff", true)
                .build();
        try {
            client = TransportClient.builder().settings(settings).build()
                    .addTransportAddress(
                            new InetSocketTransportAddress(InetAddress.getByName("192.168.241.129"),
                                    Integer.valueOf("9300"))
                    );
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public static TransportClient getClient() {
        return client;
    }
}
