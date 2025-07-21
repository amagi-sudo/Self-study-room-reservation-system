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
 * Room查询模型
 */
@NoArgsConstructor
@Data
public class RoomPagedInput extends PagedInput {
    
    /**
     * Id主键
     */
    @JsonProperty("Id")
    private Integer Id;
    /**
     * 名称模糊查询条件
     */
  	 @JsonProperty("NameLike")
    private String NameLike;
    /**
     * 地址模糊查询条件
     */
  	 @JsonProperty("AddressLike")
    private String AddressLike;
    /**
     * 介绍模糊查询条件
     */
  	 @JsonProperty("ContentLike")
    private String ContentLike;

}
