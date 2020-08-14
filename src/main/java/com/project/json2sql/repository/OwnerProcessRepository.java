package com.project.json2sql.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.json2sql.model.OwnerProcess;

public interface OwnerProcessRepository extends JpaRepository<OwnerProcess, Long>{

	@Query("select g from #{#entityName} g where g.id = :id")
	List<OwnerProcess> fetchById(String id);
}
