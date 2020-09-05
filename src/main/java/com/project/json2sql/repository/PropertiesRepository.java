package com.project.json2sql.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.json2sql.model.Properties;

@Repository
public interface PropertiesRepository extends CrudRepository<Properties, Long>{
	
	@Query("select g from #{#entityName} g where g.id = :id")
	List<Properties> fetchById(String id);

	@Query("select count(*) from #{#entityName} g")
	int getPropertiesCount();

	Object findAll(Pageable pageable);

	@Query("SELECT DISTINCT g.suburb FROM #{#entityName} g")
	List<String> getAllSuburbs();

	@Query("select g from #{#entityName} g where g.suburb = :suburbs")
	List<Properties> getAllPropertiesFromSuburbs(String suburbs);

	@Query("select g from #{#entityName} g where g.suburb = :suburbs and g.id = :id")
	List<Properties> getAllPropertiesFromSuburbsWithId(String suburbs, String id);
	
	@Transactional
	@Modifying
	@Query("update #{#entityName} g set g.isProcess = :isProcess where g.id = :id")
	void updateProcess(String id, String isProcess);
	

	@Query("select g from #{#entityName} g where isProcess = :isProcess")
	List<Properties> getInactiveProperties(String isProcess);
	
	

}
