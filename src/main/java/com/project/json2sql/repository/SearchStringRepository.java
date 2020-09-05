package com.project.json2sql.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.json2sql.model.SearchStringFile;

@Repository
public interface SearchStringRepository extends JpaRepository<SearchStringFile, Long>{

	@Query("select g from #{#entityName} g where g.status = :status and g.isProcess = :isProcess")
	List<SearchStringFile> fetchActiveString(String status, String isProcess);
}
