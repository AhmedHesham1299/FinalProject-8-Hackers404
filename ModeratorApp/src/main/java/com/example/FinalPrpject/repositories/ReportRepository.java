package com.example.FinalPrpject.repositories;

import com.example.FinalPrpject.models.Report;
import com.example.FinalPrpject.models.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByStatus(ReportStatus status);

}
