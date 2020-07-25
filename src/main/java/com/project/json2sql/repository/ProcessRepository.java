package com.project.json2sql.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.project.json2sql.model.ProcessJson;

@Repository
public interface ProcessRepository extends CrudRepository<ProcessJson, Long>{


}
