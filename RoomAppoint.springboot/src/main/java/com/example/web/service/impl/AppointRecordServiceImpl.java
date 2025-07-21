package com.example.web.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.dto.query.AppointRecordPagedInput; // 添加这一行
import com.example.web.entity.*;
import com.example.web.enums.AppointDateTypeEnum;
import com.example.web.enums.AppointStatusEnum;
import com.example.web.mapper.*;
import com.example.web.service.*;
import com.example.web.tools.dto.*;
import com.example.web.tools.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import lombok.SneakyThrows;
import com.example.web.tools.*;

/**
 * 预约记录功能实现类
 */
@Service
public class AppointRecordServiceImpl extends ServiceImpl<AppointRecordMapper, AppointRecord> implements AppointRecordService {

    /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper _AppUserMapper;
    /**
     * 操作数据库的AppointRecord表mapper对象
     */
    @Autowired
    private AppointRecordMapper _AppointRecordMpper;
    @Autowired
    private RoomMapper _RoomMapper;
    @Autowired
    private SeatMapper _SeatMapper;


    /**
     * 操作数据库的Integral表mapper对象
     */
    @Autowired
    private IntegralMapper _IntegralMpper;

    /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<AppointRecord> BuilderQuery(AppointRecordPagedInput input) {
        //声明一个支持预约记录查询的(拉姆达)表达式
        LambdaQueryWrapper<AppointRecord> queryWrapper = Wrappers.<AppointRecord>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, AppointRecord::getId, input.getId())
                .eq(input.getCreatorId() != null, AppointRecord::getCreatorId, input.getCreatorId());
        //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getNoLike())) {
            queryWrapper = queryWrapper.like(AppointRecord::getNo, input.getNoLike());
        }
        if (Extension.isNotNullOrEmpty(input.getPhoneLike())) {
            queryWrapper = queryWrapper.like(AppointRecord::getPhone, input.getPhoneLike());
        }
        if (Extension.isNotNullOrEmpty(input.getNameLike())) {
            queryWrapper = queryWrapper.like(AppointRecord::getName, input.getNameLike());
        }
        if (Extension.isNotNullOrEmpty(input.getCommentLike())) {
            queryWrapper = queryWrapper.like(AppointRecord::getSComment, input.getCommentLike());
        }

        if (input.getRoomId() != null) {
            queryWrapper = queryWrapper.eq(AppointRecord::getRoomId, input.getRoomId());
        }

        if (input.getSeatId() != null) {
            queryWrapper = queryWrapper.eq(AppointRecord::getSeatId, input.getSeatId());
        }

        if (input.getUserId() != null) {
            queryWrapper = queryWrapper.eq(AppointRecord::getUserId, input.getUserId());
        }

        if (input.getAppointStatus() != null) {
            queryWrapper = queryWrapper.eq(AppointRecord::getAppointStatus, input.getAppointStatus());
        }
        if (input.getBeginTimeRange() != null && !input.getBeginTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.lt(AppointRecord::getBeginTime, input.getBeginTimeRange().get(1));
            queryWrapper = queryWrapper.gt(AppointRecord::getBeginTime, input.getBeginTimeRange().get(0));
        }
        if (input.getEndTimeRange() != null && !input.getEndTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.lt(AppointRecord::getEndTime, input.getEndTimeRange().get(1));
            queryWrapper = queryWrapper.gt(AppointRecord::getEndTime, input.getEndTimeRange().get(0));
        }
        return queryWrapper;
    }

    /**
     * 处理预约记录对于的外键数据
     */
    private List<AppointRecordDto> DispatchItem(List<AppointRecordDto> items) throws InvocationTargetException, IllegalAccessException {

        for (AppointRecordDto item : items) {
            //查询出关联的创建用户信息

            AppUser CreatorAppUserEntity = _AppUserMapper.selectList(Wrappers.<AppUser>lambdaQuery().eq(AppUser::getId, item.getCreatorId())).stream().findFirst().orElse(new AppUser());
            item.setCreatorAppUserDto(CreatorAppUserEntity.MapToDto());

            //查询出关联的AppUser表信息
            AppUser UserEntity = _AppUserMapper.selectList(Wrappers.<AppUser>lambdaQuery().eq(AppUser::getId, item.getUserId())).stream().findFirst().orElse(new AppUser());
            item.setUserDto(UserEntity.MapToDto());


            //查询出关联的Room表信息
            Room RoomEntity = _RoomMapper.selectList(Wrappers.<Room>lambdaQuery().eq(Room::getId, item.getRoomId())).stream().findFirst().orElse(new Room());
            item.setRoomDto(RoomEntity.MapToDto());


            //查询出关联的Seat表信息
            Seat SeatEntity = _SeatMapper.selectList(Wrappers.<Seat>lambdaQuery().eq(Seat::getId, item.getSeatId())).stream().findFirst().orElse(new Seat());
            item.setSeatDto(SeatEntity.MapToDto());
        }
        return items;
    }

    /**
     * 预约记录分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<AppointRecordDto> List(AppointRecordPagedInput input) {
        //构建where条件+排序
        LambdaQueryWrapper<AppointRecord> queryWrapper = BuilderQuery(input);

        //按创建时间从大到小排序 最新的显示在最前面
        queryWrapper = queryWrapper.orderByDesc(AppointRecord::getCreationTime);
        //构建一个分页查询的model
        Page<AppointRecord> page = new Page<>(input.getPage(), input.getLimit());
        //从数据库进行分页查询获取预约记录数据
        IPage<AppointRecord> pageRecords = _AppointRecordMpper.selectPage(page, queryWrapper);
        //获取所有满足条件的数据行数
        Long totalCount = _AppointRecordMpper.selectCount(queryWrapper);
        //把AppointRecord实体转换成AppointRecord传输模型
        List<AppointRecordDto> items = Extension.copyBeanList(pageRecords.getRecords(), AppointRecordDto.class);
        //计算表的数据
        items = DispatchItem(items);


        //返回一个分页结构给前端
        return PagedResult.GetInstance(items, totalCount);

    }

    /**
     * 单个预约记录查询
     */
    @SneakyThrows
    @Override
    public AppointRecordDto Get(AppointRecordPagedInput input) {
        if (input.getId() == null) {
            return new AppointRecordDto();
        }
        PagedResult<AppointRecordDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new AppointRecordDto());
    }

    /**
     * 预约记录创建或者修改
     */
    @SneakyThrows
    @Override
    public AppointRecordDto CreateOrEdit(AppointRecordDto input) {
        //声明一个预约记录实体
        AppointRecord AppointRecord = input.MapToEntity();
        //调用数据库的增加或者修改方法
        saveOrUpdate(AppointRecord);
        //把传输模型返回给前端
        return AppointRecord.MapToDto();
    }

    /**
     * 预约记录删除
     */
    @Override
    public void Delete(IdInput input) {
        AppointRecord entity = _AppointRecordMpper.selectById(input.getId());
        _AppointRecordMpper.deleteById(entity);
    }

    /**
     * 预约记录批量删除
     */
    @Override
    public void BatchDelete(IdsInput input) {
        for (Integer id : input.getIds()) {
            IdInput idInput = new IdInput();
            idInput.setId(id);
            Delete(idInput);
        }
    }

    /**
     * 预约之前检测是否满足条件
     */
    @Override
    public void CheckIsAbleAppoint(AppointRecordDto input) {

        if (input.getAppointDateType() == AppointDateTypeEnum.上午.index()) {

            //假设是12点，你应该选在11点30分之前预约
            if (LocalDateTime.now().isAfter(input.getAppointDate().plusHours(12).plusMinutes(-30))) {
                throw new CustomException("预约时间已过，请重新选择预约时间！");
            }
        } else if (input.getAppointDateType() == AppointDateTypeEnum.下午.index()) {
            if (LocalDateTime.now().isAfter(input.getAppointDate().plusHours(18).plusMinutes(-30))) {
                throw new CustomException("预约时间已过，请重新选择预约时间！");
            }
        } else if (input.getAppointDateType() == AppointDateTypeEnum.夜晚.index()) {
            if (LocalDateTime.now().isAfter(input.getAppointDate().plusHours(23).plusMinutes(-30))) {
                throw new CustomException("预约时间已过，请重新选择预约时间！");
            }
        }

        //eq() 用于生成 SQL 查询的等值条件，确保查询结果的字段值等于指定的输入值。
        List<AppointRecord> appointRecords = _AppointRecordMpper.selectList(Wrappers.<AppointRecord>lambdaQuery().eq(AppointRecord::getRoomId, input.getRoomId())
                .eq(AppointRecord::getSeatId, input.getSeatId())
                .eq(AppointRecord::getBeginTime, input.getBeginTime())
                .eq(AppointRecord::getEndTime, input.getEndTime())
                .in(AppointRecord::getAppointStatus, AppointStatusEnum.待使用.index(), AppointStatusEnum.使用中.index()));
        if (appointRecords.stream().count() > 0) {
            throw new CustomException("该时间此座位已被预约");
        }


        Integer cancelCount = _AppUserMapper.selectById(input.getUserId()).getOverdueTimes();
        if (cancelCount != null) {
            Room room = _RoomMapper.selectById(input.getRoomId());
            if (room.getEveryMonCancelCount() < cancelCount) {
                throw new CustomException("本月取消次数超过限制！");
            }
        }


        //检测当前用户在当前时间段和自习室是否存在预约的记录，并且有效
        Long selectCount = _AppointRecordMpper.selectCount(Wrappers.<AppointRecord>lambdaQuery().eq(AppointRecord::getUserId, input.getUserId())
                .ge(AppointRecord::getAppointDate, input.getAppointDate())
                .eq(AppointRecord::getRoomId, input.getRoomId())
                .eq(AppointRecord::getAppointDateType, input.getAppointDateType())
                .in(AppointRecord::getAppointStatus, AppointStatusEnum.待使用.index(), AppointStatusEnum.使用中.index(), AppointStatusEnum.待评价.index(), AppointStatusEnum.预约完成.index()));
        if (selectCount > 0) {
            throw new CustomException("您已经预约过该自习室此时间段的座位了！");
        }

        //检测当前用户在当前时间段其他自习室是否存在预约的记录，并且有效
        selectCount = _AppointRecordMpper.selectCount(Wrappers.<AppointRecord>lambdaQuery().eq(AppointRecord::getUserId, input.getUserId())
                .ge(AppointRecord::getAppointDate, input.getAppointDate())
                .eq(AppointRecord::getAppointDateType, input.getAppointDateType())
                .in(AppointRecord::getAppointStatus, AppointStatusEnum.待使用.index(), AppointStatusEnum.使用中.index(), AppointStatusEnum.待评价.index(), AppointStatusEnum.预约完成.index()));
        if (selectCount > 0) {
            throw new CustomException("您已经预约过其他自习室该时间段的座位了！");
        }

    }


    /**
     * 选座确认
     */
    @SneakyThrows
    @Override
    public AppointRecordDto ToOrder(AppointRecordDto input) {
        //在确认预约之前检测是否满足预约条件
        this.CheckIsAbleAppoint(input);

        //生成一个年月日时分秒+随机数生成一个订单号
        String no = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + new Random().nextInt(10000);
        input.setNo("D" + no);

        //声明一个预约记录实体
        AppointRecord AppointRecord = input.MapToEntity();
        //调用数据库的增加或者修改方法
        saveOrUpdate(AppointRecord);
        //把传输模型返回给前端
        return AppointRecord.MapToDto();
    }

    /**
     * 到场
     */
    @SneakyThrows
    @Override
    public void ArrivalClock(AppointRecordDto input) {

        if (input.getAppointDateType() == AppointDateTypeEnum.上午.index()) {
            //到场时间必须是早上6点到12点
            if (LocalDateTime.now().isBefore(input.getAppointDate().plusHours(6)) || LocalDateTime.now().isAfter(input.getAppointDate().plusHours(12))) {
                throw new CustomException("打卡签到时间必须是早上6点到12点之间");
            }
        }

        if (input.getAppointDateType() == AppointDateTypeEnum.下午.index()) {
            //到场时间必须是12点到18点
            if (LocalDateTime.now().isBefore(input.getAppointDate().plusHours(14)) || LocalDateTime.now().isAfter(input.getAppointDate().plusHours(18))) {
                throw new CustomException("打卡签到时间必须是14点到18点之间");
            }
        }

        if (input.getAppointDateType() == AppointDateTypeEnum.夜晚.index()) {
            //到场时间必须是19点到23点
            if (LocalDateTime.now().isBefore(input.getAppointDate().plusHours(19)) || LocalDateTime.now().isAfter(input.getAppointDate().plusHours(23))) {
                throw new CustomException("打卡签到时间必须是19点到23点之间");
            }
        }
        input.setBeginTime(LocalDateTime.now());
        input.setAppointStatus(AppointStatusEnum.使用中.index());
        AppointRecord appointRecord = input.MapToEntity();
        saveOrUpdate(appointRecord);
    }

    /**
     * 修改用户的逾期次数
     *
     * @param userId
     */

    private void UpdateUserOverdueTimes(Integer userId) {
        AppUser appUser = _AppUserMapper.selectById(userId);
        if (appUser.getOverdueTimes() == null) {
            appUser.setOverdueTimes(0);
        }
        appUser.setOverdueTimes(appUser.getOverdueTimes() + 1);
        _AppUserMapper.updateById(appUser);
    }

    /**
     * 取消预约
     */
    @SneakyThrows
    @Override
    public void CancelAppoint(AppointRecordDto input) {

        input.setAppointStatus(AppointStatusEnum.用户取消.index());
        if (input.getAppointDateType() == AppointDateTypeEnum.上午.index()) {

            if (LocalDateTime.now().isAfter(input.getAppointDate().plusHours(6))) {
                input.setAppointStatus(AppointStatusEnum.逾期取消.index());
                UpdateUserOverdueTimes(input.getUserId());
            }
        }

        if (input.getAppointDateType() == AppointDateTypeEnum.下午.index()) {

            if (LocalDateTime.now().isBefore(input.getAppointDate().plusHours(14))) {
                input.setAppointStatus(AppointStatusEnum.逾期取消.index());
                UpdateUserOverdueTimes(input.getUserId());
            }
        }

        if (input.getAppointDateType() == AppointDateTypeEnum.夜晚.index()) {

            if (LocalDateTime.now().isBefore(input.getAppointDate().plusHours(19))) {
                input.setAppointStatus(AppointStatusEnum.逾期取消.index());
                UpdateUserOverdueTimes(input.getUserId());
            }
        }
        AppointRecord appointRecord = input.MapToEntity();
        saveOrUpdate(appointRecord);
    }

    /**
     * 提前结束
     */
    @SneakyThrows
    @Override
    public void EarlyEnd(AppointRecordDto input) {
        input.setAppointStatus(AppointStatusEnum.待评价.index());
        input.setEndTime(LocalDateTime.now());
        AppointRecord appointRecord = input.MapToEntity();
        saveOrUpdate(appointRecord);
    }

    /**
     * 评论评分
     */
    @SneakyThrows
    @Override
    public void SComment(AppointRecordDto input) {
        input.setAppointStatus(AppointStatusEnum.预约完成.index());
        AppointRecord appointRecord = input.MapToEntity();
        saveOrUpdate(appointRecord);

        Integral integral = new Integral();
        integral.setIntegralValue(2);
        integral.setSource("自习室评论评分");
        integral.setTitle("自习室评论评分获得积分+2");
        integral.setUserId(appointRecord.getUserId());
        integral.setRelativeCode(input.getNo());
        _IntegralMpper.insert(integral);
    }

    /**
     * 自动完成
     */
    @SneakyThrows
    @Override
    public void AutoCompletedAppoint() {
        //查询状态为使用中的
        List<AppointRecord> appointRecords = _AppointRecordMpper.selectList(Wrappers.<AppointRecord>lambdaQuery().eq(AppointRecord::getAppointStatus, AppointStatusEnum.使用中.index()));
        for (AppointRecord appointRecord : appointRecords) {
            if (appointRecord.getAppointDateType() == AppointDateTypeEnum.上午.index() && LocalDateTime.now().isAfter(appointRecord.getBeginTime().plusHours(12))) {
                appointRecord.setEndTime(appointRecord.getBeginTime().plusHours(12));
                appointRecord.setAppointStatus(AppointStatusEnum.待评价.index());
            }
            if (appointRecord.getAppointDateType() == AppointDateTypeEnum.下午.index() && LocalDateTime.now().isAfter(appointRecord.getBeginTime().plusHours(19))) {
                appointRecord.setEndTime(appointRecord.getBeginTime().plusHours(19));
                appointRecord.setAppointStatus(AppointStatusEnum.待评价.index());
            }
            if (appointRecord.getAppointDateType() == AppointDateTypeEnum.夜晚.index() && LocalDateTime.now().isAfter(appointRecord.getBeginTime().plusHours(23))) {
                appointRecord.setEndTime(appointRecord.getBeginTime().plusHours(23));
                appointRecord.setAppointStatus(AppointStatusEnum.待评价.index());
            }
        }
        if (appointRecords.size() > 0) {
            _AppointRecordMpper.updateById(appointRecords);
        }


    }


    /**
     * 自动逾期
     */
    @SneakyThrows
    @Override
    public void AutoOverdueTimes() {
        //查询状态为使用中的
        List<AppointRecord> appointRecords = _AppointRecordMpper.selectList(Wrappers.<AppointRecord>lambdaQuery().eq(AppointRecord::getAppointStatus, AppointStatusEnum.待使用.index()));
        for (AppointRecord appointRecord : appointRecords) {
            if (appointRecord.getAppointDateType() == AppointDateTypeEnum.上午.index() && LocalDateTime.now().isAfter(appointRecord.getBeginTime().plusHours(11))) {
                appointRecord.setAppointStatus(AppointStatusEnum.逾期取消.index());
                UpdateUserOverdueTimes(appointRecord.getUserId());
            }
            if (appointRecord.getAppointDateType() == AppointDateTypeEnum.下午.index() && LocalDateTime.now().isAfter(appointRecord.getBeginTime().plusHours(18))) {
                appointRecord.setAppointStatus(AppointStatusEnum.逾期取消.index());
                UpdateUserOverdueTimes(appointRecord.getUserId());
            }
            if (appointRecord.getAppointDateType() == AppointDateTypeEnum.夜晚.index() && LocalDateTime.now().isAfter(appointRecord.getBeginTime().plusHours(22))) {
                appointRecord.setAppointStatus(AppointStatusEnum.逾期取消.index());
                UpdateUserOverdueTimes(appointRecord.getUserId());
            }
        }
        if (appointRecords.size() > 0) {
            _AppointRecordMpper.updateById(appointRecords);
        }
    }



    /**
     * 统计早中晚每个自习室的使用率&&空闲率
     */
    @SneakyThrows
    @Override
    public List<Object> GetAppointRoomUseRate(AppointRoomUseRateQueryInput input) {
        ArrayList<Object> resultList = new ArrayList<>();

        input.setSelectDate(LocalDate.now().atTime(0, 0, 0));

        LocalDateTime beginTime = LocalDate.now().atTime(0, 0, 0);
        LocalDateTime endTime = LocalDate.now().atTime(23, 59, 59);

        List<Room> rooms = _RoomMapper.selectList(null);
        for (Room room : rooms) {
            HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
            objectObjectHashMap.put("Room", room);

            Long seatCount = _SeatMapper.selectCount(Wrappers.<Seat>lambdaQuery().eq(Seat::getRoomId, room.getId()));

            //使用率=座位的有效次数（多次预约同一个座位咱们算1次）/座位个数
            List<AppointRecord> appointRecordList = _AppointRecordMpper.selectList(Wrappers.<AppointRecord>lambdaQuery()
                    .eq(AppointRecord::getRoomId, room.getId())
                    .ge(AppointRecord::getAppointDate, beginTime)
                    .le(AppointRecord::getAppointDate, endTime)
                    .in(AppointRecord::getAppointStatus, AppointStatusEnum.使用中.index(), AppointStatusEnum.待评价.index(), AppointStatusEnum.预约完成.index()));

            //得到上午的预约的有效座位次数
            var amEffectSeatCount = appointRecordList.stream().filter(x -> x.getAppointDateType() == AppointDateTypeEnum.上午.index()).map(x -> x.getSeatId()).distinct().count();
            var amUseRate = (double) amEffectSeatCount / seatCount;
            //保留2未小数
            amUseRate = Math.round(amUseRate * 100.0);
            objectObjectHashMap.put("AmUseRate", amUseRate);

            //得到下午的预约的有效座位次数
            var pmEffectSeatCount = appointRecordList.stream().filter(x -> x.getAppointDateType() == AppointDateTypeEnum.下午.index()).map(x -> x.getSeatId()).distinct().count();
            var pmUseRate = (double) pmEffectSeatCount / seatCount;
            //保留2未小数
            pmUseRate = Math.round(pmUseRate * 100.0);
            objectObjectHashMap.put("PmUseRate", pmUseRate);

            //得到夜晚的预约的有效座位次数
            var nmEffectSeatCount = appointRecordList.stream().filter(x -> x.getAppointDateType() == AppointDateTypeEnum.夜晚.index()).map(x -> x.getSeatId()).distinct().count();
            var nmUseRate = (double) nmEffectSeatCount / seatCount;
            //保留2未小数
            nmUseRate = Math.round(nmUseRate * 100.0);
            objectObjectHashMap.put("NmUseRate", nmUseRate);

            resultList.add(objectObjectHashMap);

        }
        return resultList;
    }

    /**
     * 统计各类数据到看板
     */
    @SneakyThrows
    @Override
    public HashMap<String, Object> GetDateCollect() {
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();

        //自习室数量
        Long roomCount = _RoomMapper.selectCount(null);
        objectObjectHashMap.put("RoomCount", roomCount);

        //总开设座位数量
        Long seatCount = _SeatMapper.selectCount(null);
        objectObjectHashMap.put("SeatCount", seatCount);

        //总预约人次
        Long appointCount = _AppointRecordMpper.selectCount(Wrappers.<AppointRecord>lambdaQuery()
                .in(AppointRecord::getAppointStatus, AppointStatusEnum.使用中.index(), AppointStatusEnum.待评价.index(), AppointStatusEnum.预约完成.index()));
        objectObjectHashMap.put("AppointCount", appointCount);

        //待使用人次
        Long waitAppointCount = _AppointRecordMpper.selectCount(Wrappers.<AppointRecord>lambdaQuery().eq(AppointRecord::getAppointStatus, AppointStatusEnum.待使用.index()));
        objectObjectHashMap.put("WaitAppointCount", waitAppointCount);

        //逾期人次
        Long overdueCount = _AppointRecordMpper.selectCount(Wrappers.<AppointRecord>lambdaQuery().eq(AppointRecord::getAppointStatus, AppointStatusEnum.逾期取消.index()));
        objectObjectHashMap.put("OverdueCount", overdueCount);

        //总剩余积分
        double totalIntegral = _IntegralMpper.selectList(null).stream().mapToDouble(x -> x.getIntegralValue()).sum();
        objectObjectHashMap.put("TotalIntegral", totalIntegral);

        return objectObjectHashMap;
    }

    /**
     * 统计各个自习室预约的状态信息
     */
    @SneakyThrows
    @Override
    public List<Object> GetAppointRoomAppointStatusData(AppointRoomAppointStatusDataQueryInput input) {
        ArrayList<Object> resultList = new ArrayList<>();

        if (input.getBeginDate() == null) {
            input.setBeginDate(LocalDate.now().atTime(0, 0, 0));

        }
        if (input.getEndDate() == null) {
            input.setEndDate(LocalDate.now().atTime(23, 59, 59));
        }


        List<Room> rooms = _RoomMapper.selectList(null);
        //使用率=座位的有效次数（多次预约同一个座位咱们算1次）/座位个数
        List<AppointRecord> appointRecordList = _AppointRecordMpper.selectList(Wrappers.<AppointRecord>lambdaQuery()
                .ge(AppointRecord::getAppointDate, input.getBeginDate())
                .le(AppointRecord::getAppointDate, input.getEndDate()));

        for (Room room : rooms) {
            HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
            objectObjectHashMap.put("Room", room);

            ArrayList<Object> dataList = new ArrayList<>();
            for (AppointStatusEnum appointStatusEnum : AppointStatusEnum.values()) {
                HashMap<String, Object> typeHashMap = new HashMap<>();

                long counted = appointRecordList.stream().filter(x -> x.getRoomId().equals(room.getId()) && x.getAppointStatus() == appointStatusEnum.index()).count();


                typeHashMap.put("Name", appointStatusEnum.name());
                typeHashMap.put("Value", counted);

                dataList.add(typeHashMap);
            }

            objectObjectHashMap.put("DataList", dataList);
            resultList.add(objectObjectHashMap);
        }
        return resultList;
    }




    /**
     * 统计各个自习室实时在场人数
     */
    @SneakyThrows
    @Override
    public List<Object> GetAppointRoomRealTimeData() {
        ArrayList<Object> resultList = new ArrayList<>();

        List<Room> rooms = _RoomMapper.selectList(null);

        LocalDateTime beginTime = LocalDate.now().atTime(0, 0, 0);
        LocalDateTime endTime = LocalDate.now().atTime(23, 59, 59);

        List<AppointRecord> appointRecordList = _AppointRecordMpper.selectList(Wrappers.<AppointRecord>lambdaQuery()
                .eq(AppointRecord::getAppointStatus, AppointStatusEnum.使用中.index())
                .ge(AppointRecord::getAppointDate, beginTime)
                .le(AppointRecord::getAppointDate, endTime));
        for (Room room : rooms) {
            HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
            long counted = appointRecordList.stream().filter(x -> x.getRoomId().equals(room.getId())).count();

            objectObjectHashMap.put("Room", room);

            //在自习室的实时人数
            objectObjectHashMap.put("Value", counted);
            resultList.add(objectObjectHashMap);
        }
        return resultList;
    }

}
