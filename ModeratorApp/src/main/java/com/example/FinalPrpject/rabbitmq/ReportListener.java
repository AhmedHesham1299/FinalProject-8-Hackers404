package com.example.FinalPrpject.rabbitmq;

import com.example.FinalPrpject.models.Report;
import com.example.FinalPrpject.services.ReportService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ReportListener {

    private final ReportService reportService;

    public ReportListener(ReportService reportService) {
        this.reportService = reportService;
    }

    @RabbitListener(queues = "report_queue")
    public void issueReport(Report report) {
        reportService.createReport(report);
        System.out.println("Received and successfully saved the report from user");
    }

}
