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
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 积分功能的Service接口的定义清单
 */
public interface IntegralService extends IService<Integral> {

    /**
     * 积分的分页查询方法接口定义
     */
    public PagedResult<IntegralDto> List(IntegralPagedInput input);

    /**
     * 积分的新增或者修改方法接口定义
     */
    public IntegralDto CreateOrEdit(IntegralDto input);

    /**
     * 获取积分信息
     */
    public IntegralDto Get(IntegralPagedInput input);

    /**
     * 积分删除
     */
    public void Delete(IdInput input);

    /**
     * 积分批量删除
     */
    public void BatchDelete(IdsInput input);

    /**
     * 得到我的积分数据
     */
    MyIntegralDataDto GetMyIntegralData();

    /**
     * 清空逾期次数
     */
    void OverdueTimesClear();

    /**
     * 登录赠送2个积分
     */
    void SignGiveIntegral();

    /**
     * 统计最近30天积分的消耗和获取的折线图
     */
    @SneakyThrows
    List<Object> GetIntegralConsumeAndGainChart();


}
