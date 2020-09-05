package com.project.json2sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.project.json2sql.model.PageStringDetails;

public interface PageStringDetailsRepository extends JpaRepository<PageStringDetails, Long>{

	@Transactional
	@Modifying
	@Query("delete from #{#entityName} g where g.searchString = :searchString")
	void deletePageByString(String searchString);
	
	@Transactional
	@Modifying
	@Query("update #{#entityName} g set g.pageNumber = :pageNumber, g.pageSize = :pageSize, g.maxResult = :maxResult, g.updatedDate = :updatedDate where g.searchString = :searchString")
	void updatePageByString(String searchString, long pageNumber, long pageSize, long maxResult, String updatedDate);
	
	@Transactional
	@Modifying
	@Query("delete from #{#entityName} g where g.id = :id")
	void deleteById(long id);
	
	@Query("select count(*) from #{#entityName} g where g.searchString = :searchString")
	int countSearchString(String searchString);
	
	@Query("select g from #{#entityName} g where g.searchString = :searchString")
	PageStringDetails getSearchStringDetails(String searchString);
}
