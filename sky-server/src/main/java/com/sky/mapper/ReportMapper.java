package com.sky.mapper;

import com.sky.vo.TurnoverReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Mapper
public interface ReportMapper {

    /**
     * 获取营业额数据
     * @param condition
     * @return
     */
    public Double getTurnoverStatisticsByMap(Map<String,Object> condition);

    /**
     * 获取总用户新增量
     * @param condition
     * @return
     */
    public Integer getUserStatisticsIncrementByMap(Map<String,Object> condition);

    /**
     * 获取用户总量
     * @param endTime
     * @return
     */
    @Select("select count(id) from user where create_time < create_time")
    public Integer getUserStatisticsTotal(LocalDateTime endTime);
}
