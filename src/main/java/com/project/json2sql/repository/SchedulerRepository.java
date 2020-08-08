package com.project.json2sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.json2sql.model.ProcessScheduleJson;

@Repository
public interface SchedulerRepository extends JpaRepository<ProcessScheduleJson, Long>{

}
