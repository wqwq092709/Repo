package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

/**
 * 报表业务
 */
public interface ReportService {

    /**
     * 营业额数据接口
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin,LocalDate end);

    /**
     * 用户数据接口
     * @param begin
     * @param end
     * @return
     */
    UserReportVO getUserStatistics(LocalDate begin,LocalDate end);

    /**
     * 订单数据接口
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO getOrderStatistics(LocalDate begin,LocalDate end);

    /**
     * 销售排名前十接口
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO getSalesTop10(LocalDate begin,LocalDate end);
}
