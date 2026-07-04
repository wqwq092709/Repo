package com.sky.mapper;

import com.sky.vo.TurnoverReportVO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.Map;

@Mapper
public interface ReportMapper {

    /**
     * 获取营业额数据
     * @param condition
     * @return
     */
    public Double getTurnoverStatisticsByMap(Map<String,Object> condition);
}
