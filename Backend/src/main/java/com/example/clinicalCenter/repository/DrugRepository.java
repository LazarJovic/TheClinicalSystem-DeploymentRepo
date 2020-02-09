package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.Drug;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrugRepository extends JpaRepository<Drug, Long> {

}
