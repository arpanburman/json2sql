package com.project.json2sql.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.project.json2sql.model.Properties;

@Repository
public interface PropertiesRepository extends CrudRepository<Properties, Long>{

}
