package com.example.web.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.poi.hpsf.Decimal;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.sql.Date;
import java.sql.Timestamp;
import lombok.Data;
import java.time.LocalDateTime;
import com.example.web.dto.*;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
/**
 * 积分表
 */
@Data
@TableName("Integral")
public class Integral extends BaseEntity {

      
  	  /**
     * 标题
     */  
    @JsonProperty("Title")
    @TableField(value="Title",updateStrategy = FieldStrategy.IGNORED)
    private String Title;
      
    /**
     * 用户
     */  
    @JsonProperty("UserId")
    @TableField(value="UserId",updateStrategy = FieldStrategy.IGNORED)
    private Integer UserId;          
      
    /**
     * 积分值
     */  
    @JsonProperty("IntegralValue")
    @TableField(value="IntegralValue",updateStrategy = FieldStrategy.IGNORED)
    private Integer IntegralValue;
      
  	  /**
     * 来源
     */  
    @JsonProperty("Source")
    @TableField(value="Source",updateStrategy = FieldStrategy.IGNORED)
    private String Source;
      
  	  /**
     * 关联号
     */  
    @JsonProperty("RelativeCode")
    @TableField(value="RelativeCode",updateStrategy = FieldStrategy.IGNORED)
    private String RelativeCode;
  
    /**
     * 把积分实体转换成积分传输模型
     */
    public IntegralDto MapToDto() throws InvocationTargetException, IllegalAccessException {
        IntegralDto IntegralDto = new IntegralDto();
        BeanUtils.copyProperties(IntegralDto,this);
        return IntegralDto;
    }

}
