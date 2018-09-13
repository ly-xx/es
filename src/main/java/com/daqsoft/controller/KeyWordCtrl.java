package com.daqsoft.controller;

import com.daqsoft.commons.responseEntity.BaseResponse;
import com.daqsoft.commons.responseEntity.ResponseBuilder;
import com.daqsoft.pojo.News;
import com.daqsoft.utils.TransportClientUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 验证keyword类型
 *
 * @author liaoxiaoxia
 * @version 1.0.0
 * @date 2018-9-13 16:31
 * @since JDK 1.8
 */
@RestController
@RequestMapping(value = "/key")
public class KeyWordCtrl {

    private static final String TYPE = "news_keyword";

    /**
     * 添加文档内容
     *
     * @param news 实体信息
     * @return json
     */
    @PostMapping("/addKeyWord")
    public BaseResponse addKeyWord(News news) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String value = mapper.writeValueAsString(news);
            TransportClient client = TransportClientUtil.getClient();
            IndexResponse response = client.prepareIndex(TYPE, TYPE)
                    .setSource(value)
                    .setId(String.valueOf(news.getId()))
                    .execute().actionGet();
            return ResponseBuilder.custom().success().data(response.isCreated()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.custom().failed("error").build();
        }
    }

    /**
     * 根据keyword查询
     *
     * @param term    查询参数
     * @param request 请求
     * @return json
     */
    @GetMapping(value = "/queryByKeyword")
    public BaseResponse queryByKeyword(String term, HttpServletRequest request) {
        TransportClient client = TransportClientUtil.getClient();
        SearchResponse response = client.prepareSearch(TYPE)
                .setTypes(TYPE)
                .setExplain(true)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.termQuery("title.keyword", term))
                .setFrom(0).setSize(10)
                .execute()
                .actionGet();
        SearchHit[] searchHits = response.getHits().getHits();
        List<Map> list = new ArrayList<>();
        for (int i = 0; i < searchHits.length; i++) {
            list.add(searchHits[i].getSource());
        }
        return ResponseBuilder.custom().success().data(list).build();
    }
}
