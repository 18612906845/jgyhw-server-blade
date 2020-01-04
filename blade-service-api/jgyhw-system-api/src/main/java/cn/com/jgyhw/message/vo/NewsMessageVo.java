package cn.com.jgyhw.message.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 回复消息之图文消息
 */
@Data
@XStreamAlias("NewsMessage")
public class NewsMessageVo extends BaseMessageVo {

    /**
     * 图文消息个数，限制为8条以内
     */
    @XStreamAlias("ArticleCount")
    private String ArticleCount;

    /**
     * 多条图文消息信息，默认第一个item为大图,注意，如果图文数超过8，则将会无响应
     */
    @XStreamAlias("Articles")
    private String Articles;
}
