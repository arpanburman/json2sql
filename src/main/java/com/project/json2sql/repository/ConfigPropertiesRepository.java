package com.project.json2sql.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.project.json2sql.model.ConfigProperties;

public interface ConfigPropertiesRepository extends CrudRepository<ConfigProperties, Long>{

	@Query("select f from #{#entityName} f where f.id = :id")
	ConfigProperties fetchById(long id);
}
