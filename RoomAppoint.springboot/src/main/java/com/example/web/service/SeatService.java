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
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
/**
 * 座位功能的Service接口的定义清单
 */
public interface SeatService extends IService<Seat> {

    /**
     * 座位的分页查询方法接口定义
     */
    public PagedResult<SeatDto> List(SeatPagedInput input) ;
    /**
     * 座位的新增或者修改方法接口定义
     */
    public SeatDto CreateOrEdit(SeatDto input);

     /**
     * 获取座位信息
     */
    public SeatDto Get(SeatPagedInput input);

     /**
     *批量创建座位
     */

    @SneakyThrows
    void BatchCreate(List<SeatDto> inputs);

    /**
     * 座位删除
     */
    public void Delete(IdInput input);

    /**
     * 座位批量删除
     */
    public void BatchDelete(IdsInput input);


    /**
     * 得到今天到未来七天的日期
     * @return
     */
    List<LocalDate> GetSevenDays();

    /**
     * 得到选中日期的座位信息
     */
    SeatArrange GetArrange(SeatPagedInput input) throws InvocationTargetException,IllegalAccessException;
}
