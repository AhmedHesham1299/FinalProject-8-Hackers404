package com.example.FinalPrpject.repositories;

import com.example.FinalPrpject.models.BanPayload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BanPayloadRepository extends JpaRepository<BanPayload, Long> {

}