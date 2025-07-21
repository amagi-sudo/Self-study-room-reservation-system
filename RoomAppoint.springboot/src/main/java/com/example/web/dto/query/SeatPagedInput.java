package com.example.web.dto.query;

import com.example.web.tools.dto.PagedInput;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.List;

/**
 * Seat查询模型
 */
@NoArgsConstructor
@Data
public class SeatPagedInput extends PagedInput {
    
    /**
     * Id主键
     */
    @JsonProperty("Id")
    private Integer Id;
    /**
     * 编号模糊查询条件
     */
  	 @JsonProperty("NoLike")
    private String NoLike;
     /**
     * 自习室
     */
  	 @JsonProperty("RoomId")
    private Integer RoomId;
     /**
     * 是否维修
     */
  	 @JsonProperty("IsMaintain")
    private Boolean IsMaintain;




    /**
     * 选择的日期
     */
    @JsonProperty("SelectDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime SelectDate;


}
