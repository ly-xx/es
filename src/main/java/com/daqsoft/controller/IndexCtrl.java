package com.daqsoft.controller;

import com.daqsoft.commons.responseEntity.BaseResponse;
import com.daqsoft.commons.responseEntity.ResponseBuilder;
import com.daqsoft.utils.TransportClientUtil;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 索引操作
 *
 * @author liaoxiaoxia
 * @version 1.0.0
 * @date 2018-9-13 18:57
 * @since JDK 1.8
 */
@RestController
@RequestMapping(value = "/index")
public class IndexCtrl {

    @GetMapping(value = "/indexCreate")
    public BaseResponse indexCreate(String index) {
        try {
            TransportClient client = TransportClientUtil.getClient();
            IndicesAdminClient adminClient = client.admin().indices();
            // 判断索引是否存在
            IndicesExistsResponse existsResponse = adminClient.prepareExists("type_news").get();
            System.out.println("索引是否存在：" + existsResponse.isExists());
            // 判断类型是否存在
            TypesExistsResponse typesExistsResponse = adminClient.prepareTypesExists("type_news")
                    .setTypes("type_news").get();
            System.out.println("类型是否存在：" + typesExistsResponse.isExists());
            // 创建索引
            CreateIndexResponse createIndexResponse = adminClient.prepareCreate(index).get();
            // 设置mapping.....
            return ResponseBuilder.custom().success().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.custom().failed("失败").build();
        }

    }
}
