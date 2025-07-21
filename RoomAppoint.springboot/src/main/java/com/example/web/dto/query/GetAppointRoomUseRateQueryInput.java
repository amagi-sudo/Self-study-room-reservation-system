package com.example.web.dto.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetAppointRoomUseRateQueryInput {

    /**
     * 选座日期   @JsonProperty("SelectDate") 是一个注解，它来自 Jackson 库（用于 JSON 处理），
     *           用于指定 JSON 数据中的某个字段名称和 Java 对象中的属性字段进行映射。
     */
    @JsonProperty("SelectDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private List<LocalDateTime> SelectDate;
}
