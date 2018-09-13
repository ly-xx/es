package com.daqsoft.controller;

import com.daqsoft.commons.responseEntity.BaseResponse;
import com.daqsoft.commons.responseEntity.ResponseBuilder;
import com.daqsoft.pojo.News;
import com.daqsoft.utils.TransportClientUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ES查询
 *
 * @author liaoxiaoxia
 * @version 1.0.0
 * @date 2018-9-12 10:26
 * @since JDK 1.8
 */
@RestController
@RequestMapping(value = "/ik")
public class IkCtrl {

    private static final String TYPE = "type_news";

    /**
     * 添加文档内容
     *
     * @param news 实体信息
     * @return json
     */
    @PostMapping("/addByString")
    public BaseResponse addByString(News news) {
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
     * 添加文档内容
     *
     * @param news 实体信息
     * @return json
     */
    @PostMapping("/addByContent")
    public BaseResponse addByContent(News news) {
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("id", news.getId())
                    .field("title", news.getTitle())
                    .field("content", news.getContent())
                    .field("reply", news.getReply())
                    .endObject();
            TransportClient client = TransportClientUtil.getClient();
            IndexResponse response = client.prepareIndex(TYPE, TYPE)
                    .setSource(builder)
                    .setId(String.valueOf(news.getId()))
                    .execute().actionGet();
            return ResponseBuilder.custom().success().data(response.isCreated()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.custom().failed("error").build();
        }
    }

    /**
     * 添加文档内容
     *
     * @param news 实体信息
     * @return json
     */
    @PostMapping("/addByMap")
    public BaseResponse addByMap(News news) {
        try {
            Map objMap = new HashMap();
            objMap.put("id", news.getId());
            objMap.put("title", news.getTitle());
            objMap.put("content", news.getContent());
            objMap.put("reply", news.getReply());
            TransportClient client = TransportClientUtil.getClient();
            IndexResponse response = client.prepareIndex(TYPE, TYPE)
                    .setSource(objMap)
                    .setId(String.valueOf(news.getId()))
                    .execute().actionGet();
            return ResponseBuilder.custom().success().data(response.isCreated()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.custom().failed("error").build();
        }
    }

    /**
     * 按词项查询
     *
     * @param term    关键字
     * @param request 请求
     * @return json
     */
    @GetMapping(value = "/getByTerm")
    public BaseResponse queryByTerm(String term, HttpServletRequest request) {
        TransportClient client = TransportClientUtil.getClient();
        SearchResponse response = client.prepareSearch(TYPE)
                .setTypes(TYPE)
                .setExplain(true)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.termQuery("title", term))
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


    /**
     * 根据文档id获取数据
     *
     * @param id      文档id
     * @param request 请求
     * @return json
     */
    @GetMapping(value = "/getById")
    public BaseResponse getById(String id, HttpServletRequest request) {
        TransportClient client = TransportClientUtil.getClient();
        GetRequestBuilder requestBuilder = client.prepareGet(TYPE, TYPE, id);
        GetResponse response = requestBuilder.execute().actionGet();
        return ResponseBuilder.custom().success().data(response.getSourceAsString()).build();
    }


    /**
     * 根据文档id删除数据
     *
     * @param id      文档id
     * @param request 请求
     * @return json
     */
    @DeleteMapping(value = "/delete")
    public BaseResponse delete(String id, HttpServletRequest request) {
        TransportClient client = TransportClientUtil.getClient();
        DeleteResponse requestBuilder = client.prepareDelete(TYPE, TYPE, id).get();
        return ResponseBuilder.custom().success().data(requestBuilder.getId()).build();
    }

    /**
     * 修改文档
     *
     * @param news 文档id
     * @return json
     */
    @PutMapping(value = "/update")
    public BaseResponse update(News news) {
        try {
            String json = new ObjectMapper().writeValueAsString(news);
            // 方式一
            UpdateRequest request = new UpdateRequest();
            request.index(TYPE)
                    .type(TYPE)
                    .id(news.getId() + "")
                    .doc(json);
            TransportClient client = TransportClientUtil.getClient();
            client.update(request).get();
            // 方式二
//            UpdateRequestBuilder requestBuilder = client.prepareUpdate(TYPE, TYPE, news.getId() + "");
//            requestBuilder.setDoc(json).get();
            // 方式三 通过script更新……
            return ResponseBuilder.custom().success().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBuilder.custom().failed("error").build();
        }
    }

    /**
     * 批量获取
     *
     * @return
     */
    @GetMapping("/mulitGet")
    public BaseResponse mulitGet() {
        TransportClient client = TransportClientUtil.getClient();
        MultiGetResponse getItemResponses = client.prepareMultiGet()
                .add("type_news", "type_news", "1")
                .add("type_news", "type_news", "2", "3", "4")
                .add("news_keyword", "news_keyword", "1")
                .get();
        List<String> list = new ArrayList<>();
        for (MultiGetItemResponse itemRespons : getItemResponses) {
            GetResponse response = itemRespons.getResponse();
            if (null != response && response.isExists()) {
                list.add(response.getSourceAsString());
            }
        }
        return ResponseBuilder.custom().success().data(list).build();
    }

    /**
     * 批量操作
     *
     * @return
     */
    @GetMapping("/mulit")
    public BaseResponse mulitAdd() {
        TransportClient client = TransportClientUtil.getClient();
        BulkRequestBuilder  prepareBulk = client.prepareBulk();
        // 添加
        IndexRequestBuilder indexRequest = client.prepareIndex(TYPE, TYPE, "id").setSource("文档数据");
        // 删除
        DeleteRequestBuilder deleteRequest = client.prepareDelete(TYPE, TYPE, "id");
        // 修改
        UpdateRequestBuilder updateRequest = client.prepareUpdate(TYPE, TYPE, "id").setDoc("修改的文档数据");
        prepareBulk.add(indexRequest).add(deleteRequest).add(updateRequest).execute().actionGet();
        return ResponseBuilder.custom().success().build();
    }

}
