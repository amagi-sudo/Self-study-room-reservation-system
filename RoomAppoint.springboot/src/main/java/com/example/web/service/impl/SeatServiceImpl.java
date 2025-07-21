package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
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
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;

import com.example.web.tools.*;

/**
 * 座位功能实现类
 */
@Service
public class SeatServiceImpl extends ServiceImpl<SeatMapper, Seat> implements SeatService {

    /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper _AppUserMapper;
    /**
     * 操作数据库的Seat表mapper对象
     */
    @Autowired
    private SeatMapper _SeatMpper;
    @Autowired
    private RoomMapper _RoomMapper;
    @Autowired
    private AppointRecordMapper _AppointRecordMapper;


    /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<Seat> BuilderQuery(SeatPagedInput input) {
        //声明一个支持座位查询的(拉姆达)表达式
        LambdaQueryWrapper<Seat> queryWrapper = Wrappers.<Seat>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, Seat::getId, input.getId())
                .eq(input.getCreatorId() != null, Seat::getCreatorId, input.getCreatorId());
        //如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getNoLike())) {
            queryWrapper = queryWrapper.like(Seat::getNo, input.getNoLike());
        }

        if (input.getRoomId() != null) {
            queryWrapper = queryWrapper.eq(Seat::getRoomId, input.getRoomId());
        }
        if (input.getIsMaintain() != null) {
            queryWrapper = queryWrapper.eq(Seat::getIsMaintain, input.getIsMaintain());
        }
        return queryWrapper;
    }

    /**
     * 处理座位对于的外键数据
     */
    private List<SeatDto> DispatchItem(List<SeatDto> items) throws InvocationTargetException, IllegalAccessException {

        for (SeatDto item : items) {
            //查询出关联的创建用户信息

            AppUser CreatorAppUserEntity = _AppUserMapper.selectList(Wrappers.<AppUser>lambdaQuery().eq(AppUser::getId, item.getCreatorId())).stream().findFirst().orElse(new AppUser());
            item.setCreatorAppUserDto(CreatorAppUserEntity.MapToDto());

            //查询出关联的Room表信息
            Room RoomEntity = _RoomMapper.selectList(Wrappers.<Room>lambdaQuery().eq(Room::getId, item.getRoomId())).stream().findFirst().orElse(new Room());
            item.setRoomDto(RoomEntity.MapToDto());

            List<AppointRecord> appointRecords = _AppointRecordMapper.selectList(Wrappers.<AppointRecord>lambdaQuery().eq(AppointRecord::getSeatId, item.getId()));

            //总评论个数
            // 计算 appointRecords 中预约状态为 "预约完成" 的记录数量
            long commentCount = appointRecords.stream()
                    .filter(x -> x.getAppointStatus() == AppointStatusEnum.预约完成.index()) // 过滤出预约完成的记录
                    .count(); // 统计满足条件的记录数量

            item.setTotalComment((int) commentCount);

            //总使用次数
            long useCount = appointRecords.stream().filter(x -> x.getAppointStatus() == AppointStatusEnum.预约完成.index()||x.getAppointStatus()==AppointStatusEnum.待评价.index()).count();
            item.setTotalUseCount((int) useCount);

            //求评分平均值
            double avgCommentScore = appointRecords.stream().filter(x -> x.getAppointStatus() == AppointStatusEnum.预约完成.index()).mapToDouble(x -> x.getCommentScore()).average().orElse(0);

            //保留2未小数
            DecimalFormat df=new DecimalFormat("#.00");
            avgCommentScore=Double.parseDouble(df.format(avgCommentScore));
            item.setAgvCommentScore(avgCommentScore);
        }
        return items;
    }

    /**
     * 座位分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<SeatDto> List(SeatPagedInput input) {
        //构建where条件+排序
        LambdaQueryWrapper<Seat> queryWrapper = BuilderQuery(input);

        //按创建时间从大到小排序 最新的显示在最前面
        queryWrapper = queryWrapper.orderByDesc(Seat::getCreationTime);
        //构建一个分页查询的model
        Page<Seat> page = new Page<>(input.getPage(), input.getLimit());
        //从数据库进行分页查询获取座位数据
        IPage<Seat> pageRecords = _SeatMpper.selectPage(page, queryWrapper);
        //获取所有满足条件的数据行数
        Long totalCount = _SeatMpper.selectCount(queryWrapper);
        //把Seat实体转换成Seat传输模型
        List<SeatDto> items = Extension.copyBeanList(pageRecords.getRecords(), SeatDto.class);
        //计算表的数据
        items = DispatchItem(items);


        //返回一个分页结构给前端
        return PagedResult.GetInstance(items, totalCount);

    }

    /**
     * 单个座位查询
     */
    @SneakyThrows
    @Override
    public SeatDto Get(SeatPagedInput input) {
        if (input.getId() == null) {
            return new SeatDto();
        }
        PagedResult<SeatDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new SeatDto());
    }

    /**
     * 检测座位是否相同
     *
     * @Param input
     */
    private void CheckSeatIsSame(SeatDto input) {
        Seat seat = _SeatMpper.selectList(Wrappers.<Seat>lambdaQuery()
                .ne(input.getId() != null, Seat::getId, input.getId())
                .eq(Seat::getSRow, input.getSRow())
                .eq(Seat::getSCol, input.getSCol())
                .eq(Seat::getRoomId, input.getRoomId())).stream().findFirst().orElse(null);
        if (seat != null) {
            throw new CustomException("该自习室已存在相同的座位号！" + seat.getNo());
        }
    }

    /**
     * 座位创建或者修改
     */

    @SneakyThrows
    @Override
    public SeatDto CreateOrEdit(SeatDto input) {
        // Step 1: 检查是否已存在相同座位号的记录
        Seat seat = _SeatMpper.selectOne(Wrappers.<Seat>lambdaQuery()
                .ne(Seat::getId, input.getId())   // 确保排除当前正在编辑的座位
                .eq(Seat::getSRow, input.getSRow()) // 行号匹配
                .eq(Seat::getSCol, input.getSCol()) // 列号匹配
                .eq(Seat::getRoomId, input.getRoomId())); // 房间ID匹配
        // 如果找到了相同的座位记录，抛出异常
        if (seat != null) {
            throw new CustomException("该自习室已存在相同的座位号！");
        }

        // Step 2: 设置座位编号
        input.setNo("S" + "-" + input.getSRow() + "-" + input.getSCol());

        // Step 3: 将输入的 DTO 转换为实体对象
        Seat Seat = input.MapToEntity();

        // Step 4: 调用数据库的保存或更新方法
        saveOrUpdate(Seat);

        // Step 5: 将实体对象转换回 DTO 并返回给前端
        return Seat.MapToDto();
    }


    /**
     * 批量创建
     */
    @SneakyThrows
//   这是 Lombok 提供的一个注解，作用是自动处理方法中的异常，不需要显式地声明 throws。
    @Override
    public void BatchCreate(List<SeatDto> inputs) {

        //因为我们没有事务的概念，先进行判断，如果集合的数据都满足条件，就不报错
        for (SeatDto input : inputs) {
            CheckSeatIsSame(input);
        }

        for (SeatDto input : inputs) {
            input.setNo("S" + "-" + input.getSRow() + "-" + input.getSCol());
        }
        List<Seat> seats = Extension.copyBeanList(inputs, Seat.class);

        //一次性创建

        saveBatch(seats);
    }

    /**
     * 座位删除
     */
    @Override
    public void Delete(IdInput input) {
        Seat entity = _SeatMpper.selectById(input.getId());
        _SeatMpper.deleteById(entity);
    }

    /**
     * 座位批量删除
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
     * 得到最近未来7天的日期
     **/
    @Override
    public List<LocalDate> GetSevenDays() {

        //得到今天到未来7天的日期聚合
        List<LocalDate> result = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            result.add(LocalDate.now().plusDays(i));
        }
        return result;
    }

    /**
     * 得到选项
     */
    @Override
    public SeatArrange GetArrange(SeatPagedInput input) throws InvocationTargetException, IllegalAccessException {
        // 得到自习室下面所有的座位信息
        List<Seat> seats = _SeatMpper.selectList(Wrappers.<Seat>lambdaQuery().eq(Seat::getRoomId, input.getRoomId()));

        // 查询选择日期的预约情况
        List<AppointRecord> appointRecords = _AppointRecordMapper.selectList(
                Wrappers.<AppointRecord>lambdaQuery().eq(AppointRecord::getRoomId, input.getRoomId())
                        .ge(AppointRecord::getAppointDate, input.getSelectDate())
                        .isNotNull(AppointRecord::getSeatId) // 添加这一行以过滤掉 seatId 为 null 的记录
        );

        List<List<SeatDto>> amSeatDtoList = new ArrayList<>();
        List<List<SeatDto>> pmSeatDtoList = new ArrayList<>();
        List<List<SeatDto>> nmSeatDtoList = new ArrayList<>();

        // 得到 seats 中的最大行和列
        int maxRow = seats.stream().mapToInt(Seat::getSRow).max().orElse(0);
        int maxCol = seats.stream().mapToInt(Seat::getSCol).max().orElse(0);
        for (int i = 1; i <= maxRow; i++) {
            int row = i;
            List<SeatDto> amSeatRowDtoList = new ArrayList<>();
            List<SeatDto> pmSeatRowDtoList = new ArrayList<>();
            List<SeatDto> nmSeatRowDtoList = new ArrayList<>();

            // 6点到12点是中午，14点到18点是下午，19点到23点是晚上
            for (int j = 1; j <= maxCol; j++) {
                int col = j;
                var seat = seats.stream().filter(x -> x.getSRow() == row && x.getSCol() == col).findFirst().orElse(new Seat());
                // 处理上午的座位
                SeatDto amSeatDto = seat.MapToDto();
                // 默认就是来占用的
                amSeatDto.setIsOccupy(false);
                if (!Extension.isNullOrZero(amSeatDto.getId())) {
                    long count = appointRecords.stream()
                            .filter(x -> x.getSeatId() != null && x.getSeatId().equals(amSeatDto.getId()) && x.getAppointDateType() == AppointDateTypeEnum.上午.index())
                            .filter(x -> (x.getAppointStatus()== AppointStatusEnum.使用中.index()
                                    || x.getAppointStatus()== AppointStatusEnum.待使用.index()
                                    || x.getAppointStatus()== AppointStatusEnum.待评价.index()
                                    || x.getAppointStatus()== AppointStatusEnum.预约完成.index()))
                            .count();
                    amSeatDto.setIsOccupy(count > 0);
                }
                amSeatRowDtoList.add(amSeatDto);

                // 处理下午
                SeatDto pmSeatDto = seat.MapToDto();
                // 默认就是来占用的
                pmSeatDto.setIsOccupy(false);
                if (!Extension.isNullOrZero(pmSeatDto.getId())) {
                    long count = appointRecords.stream()
                            .filter(x -> x.getSeatId() != null && x.getSeatId().equals(pmSeatDto.getId()) && x.getAppointDateType() == AppointDateTypeEnum.下午.index())
                            .filter(x -> (x.getAppointStatus()== AppointStatusEnum.使用中.index()
                                    || x.getAppointStatus()== AppointStatusEnum.待使用.index()
                                    || x.getAppointStatus()== AppointStatusEnum.待评价.index()
                                    || x.getAppointStatus()== AppointStatusEnum.预约完成.index()))
                            .count();
                    pmSeatDto.setIsOccupy(count > 0);
                }
                pmSeatRowDtoList.add(pmSeatDto);

                // 处理晚上
                SeatDto nmSeatDto = seat.MapToDto();
                // 默认就是来占用的
                nmSeatDto.setIsOccupy(false);
                if (!Extension.isNullOrZero(nmSeatDto.getId())) {
                    long count = appointRecords.stream()
                            .filter(x -> x.getSeatId() != null && x.getSeatId().equals(nmSeatDto.getId()) && x.getAppointDateType() == AppointDateTypeEnum.夜晚.index())
                            .filter(x -> (x.getAppointStatus()== AppointStatusEnum.使用中.index()
                                    || x.getAppointStatus()== AppointStatusEnum.待使用.index()
                                    || x.getAppointStatus()== AppointStatusEnum.待评价.index()
                                    || x.getAppointStatus()== AppointStatusEnum.预约完成.index()))
                            .count();
                    nmSeatDto.setIsOccupy(count > 0);
                }
                nmSeatRowDtoList.add(nmSeatDto);
            }
            amSeatDtoList.add(amSeatRowDtoList);
            pmSeatDtoList.add(pmSeatRowDtoList);
            nmSeatDtoList.add(nmSeatRowDtoList);
        }

        SeatArrange seatArrange = new SeatArrange();
        seatArrange.setAmSeatDtoList(amSeatDtoList);
        seatArrange.setPmSeatDtoList(pmSeatDtoList);
        seatArrange.setNmSeatDtoList(nmSeatDtoList);
        return seatArrange;
    }



}
