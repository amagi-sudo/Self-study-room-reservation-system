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
 * 自习室类
 */
@Data
public class RoomDto extends BaseDto
{

    
     
    /**
     * 名称
     */ 
    @JsonProperty("Name")
    private String Name;
    
     
    /**
     * 封面
     */ 
    @JsonProperty("Cover")
    private String Cover;
    
     
    /**
     * 地址
     */ 
    @JsonProperty("Address")
    private String Address;
    
     
    /**
     * 介绍
     */ 
    @JsonProperty("Content")
    private String Content;

    /**
     * 座位个数
     */
    @JsonProperty("SeatCount")
    private Integer SeatCount;


     
    /**
     * 每月可取消次数
     */ 
    @JsonProperty("EveryMonCancelCount")
    private Integer EveryMonCancelCount;          

 	 /**
     * 把自习室传输模型转换成自习室实体
     */
    public Room MapToEntity() throws InvocationTargetException, IllegalAccessException {
        Room Room= new Room();
        BeanUtils.copyProperties(Room,this);
        return Room;
    }

}
