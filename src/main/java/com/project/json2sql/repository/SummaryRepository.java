package com.project.json2sql.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.project.json2sql.model.Summary;

@Repository
public interface SummaryRepository extends CrudRepository<Summary, Long>{

}
