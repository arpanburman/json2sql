package com.project.json2sql.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.json2sql.model.OwnerDetails;

public interface OwnerDetailsRepository extends JpaRepository<OwnerDetails, Long>{
	
	@Query("select g from #{#entityName} g where g.id = :id")
	List<OwnerDetails> fetchById(String id);

	@Query("select count(*) from #{#entityName} g")
	int getOwnerCount();


}
