package com.daqsoft.pojo;

/**
 * 新闻信息
 *
 * @author liaoxiaoxia
 * @version 1.0.0
 * @date 2018-9-12 10:35
 * @since JDK 1.8
 */
public class News {

    /**
     * 主键
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 回复
     */
    private Long reply;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getReply() {
        return reply;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }
}
