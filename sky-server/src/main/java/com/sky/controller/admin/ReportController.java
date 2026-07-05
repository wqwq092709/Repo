package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;

@Api(tags = "报表统计")
@RestController
@RequestMapping("/admin/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 展示营业额接口
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("展示营业额接口")
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverReport(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        TurnoverReportVO turnoverStatistics = reportService.getTurnoverStatistics(begin, end);
        return Result.success(turnoverStatistics);
    }

    /**
     * 用户统计接口
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("用户统计接口")
    @GetMapping("/userStatistics")
    public Result<UserReportVO> userReport(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){

        UserReportVO userStatisticsVO = reportService.getUserStatistics(begin, end);
        return Result.success(userStatisticsVO);

    }

    /**
     * 订单统计接口
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("订单统计")
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> orderReport(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        OrderReportVO orderStatistics = reportService.getOrderStatistics(begin, end);
        return Result.success(orderStatistics);
    }

    @ApiOperation("销售排名前十接口")
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> SalesTop10Report(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        SalesTop10ReportVO salesTop10 = reportService.getSalesTop10(begin, end);
        return Result.success(salesTop10);
    }
}
