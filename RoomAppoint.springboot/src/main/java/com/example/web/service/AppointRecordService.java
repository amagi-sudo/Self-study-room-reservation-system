package com.example.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.entity.*;
import com.example.web.tools.dto.*;
import com.example.web.enums.*;

import java.lang.reflect.InvocationTargetException;

import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 预约记录功能的Service接口的定义清单
 */
public interface AppointRecordService extends IService<AppointRecord> {

    /**
     * 预约记录的分页查询方法接口定义
     */
    public PagedResult<AppointRecordDto> List(AppointRecordPagedInput input);

    /**
     * 预约记录的新增或者修改方法接口定义
     */
    public AppointRecordDto CreateOrEdit(AppointRecordDto input);

    /**
     * 获取预约记录信息
     */
    public AppointRecordDto Get(AppointRecordPagedInput input);

    /**
     * 预约记录删除
     */
    public void Delete(IdInput input);

    /**
     * 预约记录批量删除
     */
    public void BatchDelete(IdsInput input);


    /**
     * 预约之前检测是否满足条件
     */
    void CheckIsAbleAppoint(AppointRecordDto input);


    /**
     * 选座确认
     */

    @SneakyThrows
    AppointRecordDto ToOrder(AppointRecordDto input);

    /**
     * 到场
     */
    @SneakyThrows
    void ArrivalClock(AppointRecordDto input);

    /**
     * 取消预约
     */
    @SneakyThrows
    void CancelAppoint(AppointRecordDto input);

    /**
     * 提前结束
     */
    @SneakyThrows
    void EarlyEnd(AppointRecordDto input);

    /**
     * 评论评分
     */
    @SneakyThrows
    void SComment(AppointRecordDto input);

    /**
     * 自动完成
     */
    public void AutoCompletedAppoint();


    /**
     * 自动逾期
     */
    @SneakyThrows
    public void AutoOverdueTimes();



    /**
     * 统计早中晚每个自习室的使用率&&空闲率
     */
    @SneakyThrows
    List<Object> GetAppointRoomUseRate(AppointRoomUseRateQueryInput input);

    /**
     * 统计各类数据到看板
     */
    @SneakyThrows
    HashMap<String, Object> GetDateCollect();

    /**
     * 统计各个自习室预约的状态信息
     */
    @SneakyThrows
    List<Object> GetAppointRoomAppointStatusData(AppointRoomAppointStatusDataQueryInput input);



    /**
     * 统计各个自习室实时在场人数
     */
    @SneakyThrows
    List<Object> GetAppointRoomRealTimeData();
}
