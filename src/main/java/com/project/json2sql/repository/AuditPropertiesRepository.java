package com.project.json2sql.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.json2sql.model.AuditProperties;

@Repository
public interface AuditPropertiesRepository extends JpaRepository<AuditProperties, Long>{

	@Query("select g from #{#entityName} g where g.id = :id and g.isActive= :status")
	List<AuditProperties> fetchById(String id, String status);
	
	@Transactional
	@Modifying
	@Query("update #{#entityName} g set g.isActive= :status where g.id = :id")
	void updateStatus(String id, String status);

}
