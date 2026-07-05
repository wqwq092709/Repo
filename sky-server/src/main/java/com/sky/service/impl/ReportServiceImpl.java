package com.sky.service.impl;


import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
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
                .totalUserList(StringUtils.join(userStatisticsTotalList, ","))
                .newUserList(StringUtils.join(incrementUserList,","))
                .build();
        return userReportVO;
    }

    /**
     * 订单数据量
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {

        //获取日期
        LocalDate tempBegin = begin;
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(tempBegin);
        while (!tempBegin.equals(end)){
            tempBegin = tempBegin.plusDays(1);
            dateList.add(tempBegin);
        }

        //每日订单数
        Map<String,Object> orderCondition = new HashMap<>();
        List<Integer> orderDayCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            orderCondition.put("begin",beginTime);
            orderCondition.put("end",endTime);
            Integer orderDayCount = reportMapper.getOrderStatistics(orderCondition);
            orderDayCount = orderDayCount == null ? 0 : orderDayCount;
            orderDayCountList.add(orderDayCount);
        }

        //有效订单数
        Map<String,Object> validOrderCondition = new HashMap<>();
        List<Integer> validOrderDayCountList = new ArrayList<>();

        Integer orderStatus = Orders.COMPLETED;
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            validOrderCondition.put("begin",beginTime);
            validOrderCondition.put("end",endTime);
            validOrderCondition.put("status", orderStatus);
            Integer validOrderDayCount = reportMapper.validOrderDayCount(validOrderCondition);
            validOrderDayCount = validOrderDayCount == null ? 0 : validOrderDayCount;
            validOrderDayCountList.add(validOrderDayCount);
        }

        //订单总数
        int orderTotal = orderDayCountList.stream().mapToInt(Integer::intValue).sum();
        //有效订单数
        int validOrderTotal = validOrderDayCountList.stream().mapToInt(Integer::intValue).sum();
        //订单完成率
        double orderCompletionRate = orderTotal == 0 ? 0.0 : (double) validOrderTotal / orderTotal;


        //封装数据
        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderDayCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderDayCountList, ","))
                .validOrderCount(validOrderTotal)
                .totalOrderCount(orderTotal)
                .orderCompletionRate(orderCompletionRate)
                .build();

        return orderReportVO;
    }

    /**
     * 销售排名前10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        Map<String,Object> getSalesTop10Condition = new HashMap<>();
        getSalesTop10Condition.put("begin",beginTime);
        getSalesTop10Condition.put("end",endTime);
        List<GoodsSalesDTO> salesTop10List = reportMapper.getSalesTop10(getSalesTop10Condition);

        List<String> goodsName = new ArrayList<>();
        List<Integer> goodsNumber = new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : salesTop10List) {
            String name = goodsSalesDTO.getName();
            goodsName.add(name);
            Integer number = goodsSalesDTO.getNumber();
            goodsNumber.add(number);
        }

        String goodsNamesString = StringUtils.join(goodsName, ",");
        String goodsNumberString = StringUtils.join(goodsNumber, ",");
        SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO
                .builder()
                .nameList(goodsNamesString)
                .numberList(goodsNumberString)
                .build();

        return salesTop10ReportVO;
    }


}
