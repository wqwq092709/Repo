package com.sky.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


import java.time.LocalDateTime;

import java.util.Map;

@Mapper
public interface ReportMapper {

    /**
     * 获取营业额数据
     *
     * @param condition
     * @return
     */
    Double getTurnoverStatisticsByMap(Map<String, Object> condition);

    /**
     * 获取总用户新增量
     *
     * @param condition
     * @return
     */
    Integer getUserStatisticsIncrementByMap(Map<String, Object> condition);

    /**
     * 获取用户总量
     *
     * @param endTime
     * @return
     */
    @Select("select count(id) from user where create_time < #{endTime}")
    Integer getUserStatisticsTotal(LocalDateTime endTime);

    /**
     * 订单数据量
     *
     * @param condition
     * @return
     */
    Integer getOrderStatistics(Map<String, Object> condition);

    /**
     *当日有效订单数
     * @param vailOrderCondition
     * @return
     */
    Integer validOrderDayCount(Map<String, Object> vailOrderCondition);
}
