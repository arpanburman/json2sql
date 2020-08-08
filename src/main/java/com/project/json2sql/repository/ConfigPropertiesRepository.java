package com.project.json2sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.json2sql.model.ConfigProperties;

public interface ConfigPropertiesRepository extends JpaRepository<ConfigProperties, Long>{

	@Query("select f from #{#entityName} f where f.id = :id")
	ConfigProperties fetchById(long id);
}
