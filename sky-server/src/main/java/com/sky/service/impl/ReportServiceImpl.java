package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {


    @Autowired
    private ReportMapper reportMapper;

    /**
     * 营业额数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        //根据日期范围获取范围内的每一天
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //获取营业额
        Map<String,Object> condition = new HashMap<>();
        List<Double> turnoverStatisticsList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            condition.put("begin",beginTime);
            condition.put("end",endTime);
            condition.put("status", Orders.COMPLETED);
            Double turnoverStatistics = reportMapper.getTurnoverStatisticsByMap(condition);
            turnoverStatistics = turnoverStatistics == null ? 0.0 : turnoverStatistics;
            turnoverStatisticsList.add(turnoverStatistics);
        }

        //封装数据
        TurnoverReportVO reportVO = TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverStatisticsList, ","))
                .build();

        return reportVO;
    }

    /**
     * 用户数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {

        //获取范围内日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //获取新增用户量
        List<Integer> incrementUserList = new ArrayList<>();
        Map<String,Object> condition = new HashMap<>();
        LocalDateTime beginTime;
        LocalDateTime endTime;
        for (LocalDate date : dateList) {
            beginTime = LocalDateTime.of(date, LocalTime.MIN);
            endTime = LocalDateTime.of(date, LocalTime.MAX);
            condition.put("begin",beginTime);
            condition.put("end",endTime);
            Integer userStatisticsCount = reportMapper.getUserStatisticsIncrementByMap(condition);
            userStatisticsCount = userStatisticsCount == null ? 0 : userStatisticsCount;
            incrementUserList.add(userStatisticsCount);
        }

        //获取每天的总用户量
        List<Integer> userStatisticsTotalList= new ArrayList<>();
        for (LocalDate date : dateList) {
            endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer userStatisticsTotal = reportMapper.getUserStatisticsTotal(endTime);
            userStatisticsTotal = userStatisticsTotal == null ? 0 : userStatisticsTotal;
            userStatisticsTotalList.add(userStatisticsTotal);
        }

        //封装数据

        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(incrementUserList, ","))
                .newUserList(StringUtils.join(userStatisticsTotalList,","))
                .build();
        return userReportVO;
    }
}
