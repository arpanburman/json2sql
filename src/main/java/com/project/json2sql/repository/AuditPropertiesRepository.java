package com.project.json2sql.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.json2sql.model.AuditProperties;

@Repository
public interface AuditPropertiesRepository extends JpaRepository<AuditProperties, Long>{

	@Query("select g from #{#entityName} g where g.id = :id")
	List<AuditProperties> fetchById(String id);

}
