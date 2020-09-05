package com.project.json2sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.project.json2sql.model.ConfigProperties;

public interface ConfigPropertiesRepository extends JpaRepository<ConfigProperties, Long>{
	
	@Query("select f from #{#entityName} f where f.id = :id")
	ConfigProperties fetchById(long id);
	
	@Transactional
	@Modifying
	@Query("update #{#entityName} g set g.starttime = :time where g.id = :id")
	void updateStopConfigDetails(long id, String time);//, g.endtime = :time
}
