package com.project.json2sql.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.project.json2sql.model.AuditOwnerDetails;

public interface AuditOwnerDetailsRepository extends CrudRepository<AuditOwnerDetails, Long>{
	
	@Query("select g from #{#entityName} g where g.id = :id")
	List<AuditOwnerDetails> fetchById(String id);

	@Query("select count(*) from #{#entityName} g")
	int getOwnerCount();


}
