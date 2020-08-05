package com.project.json2sql.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.project.json2sql.model.Properties;

@Repository
public interface PropertiesRepository extends CrudRepository<Properties, Long>{
	
	@Query("select g from #{#entityName} g where g.id = :id")
	List<Properties> fetchById(String id);

	@Query("select count(*) from #{#entityName} g")
	int getPropertiesCount();

}
