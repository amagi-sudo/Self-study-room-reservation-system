package com.example.web.dto.query;

import com.example.web.tools.dto.PagedInput;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.List;

/**
 * Integral查询模型
 */
@NoArgsConstructor
@Data
public class IntegralPagedInput extends PagedInput {
    
    /**
     * Id主键
     */
    @JsonProperty("Id")
    private Integer Id;
    /**
     * 标题模糊查询条件
     */
  	 @JsonProperty("TitleLike")
    private String TitleLike;
    /**
     * 来源模糊查询条件
     */
  	 @JsonProperty("SourceLike")
    private String SourceLike;
    /**
     * 关联号模糊查询条件
     */
  	 @JsonProperty("RelativeCodeLike")
    private String RelativeCodeLike;
     /**
     * 用户
     */
  	 @JsonProperty("UserId")
    private Integer UserId;

}
