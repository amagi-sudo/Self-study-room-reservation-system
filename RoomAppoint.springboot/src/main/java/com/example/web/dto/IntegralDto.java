package com.example.web.dto;
import com.example.web.enums.*;
import com.example.web.tools.dto.BaseDto;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.sql.Date;
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.example.web.entity.*;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
/**
 * 积分类
 */
@Data
public class IntegralDto extends BaseDto
{

    
     
    /**
     * 标题
     */ 
    @JsonProperty("Title")
    private String Title;
    
     
    /**
     * 用户
     */ 
    @JsonProperty("UserId")
    private Integer UserId;          
    
     
    /**
     * 积分值
     */ 
    @JsonProperty("IntegralValue")
    private Integer IntegralValue;          
    
     
    /**
     * 来源
     */ 
    @JsonProperty("Source")
    private String Source;
    
     
    /**
     * 关联号
     */ 
    @JsonProperty("RelativeCode")
    private String RelativeCode;

     @JsonProperty("UserDto") 
    private AppUserDto UserDto;                        
   
 	 /**
     * 把积分传输模型转换成积分实体
     */
    public Integral MapToEntity() throws InvocationTargetException, IllegalAccessException {
        Integral Integral= new Integral();
        BeanUtils.copyProperties(Integral,this);
        return Integral;
    }

}
