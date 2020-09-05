package com.project.json2sql.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.json2sql.model.Properties;
import com.project.json2sql.model.SearchString;

@Repository
public interface SearchStringRecordsRepository extends JpaRepository<SearchString, Long>{

	@Query("select g from #{#entityName} g where g.searchString = :searchString")
	List<SearchString> fetchStringById(String searchString);
	
	@Query("select count(*) from #{#entityName} g where g.searchString = :searchString")
	int getStringCount(String searchString);

	@Query("select count(*) from #{#entityName} g")
	int countSearchString();
	
	@Transactional
	@Modifying
	@Query("update #{#entityName} g set g.lastReadPage = :lastPage where g.searchString = :searchString")
	void updateSearchString(String searchString, long lastPage);

	@Query("select g from #{#entityName} g where isProcess = :isProcess")
	List<SearchString> getInactiveSearchString(String isProcess);
	
	@Transactional
	@Modifying
	@Query("delete from #{#entityName} g where g.searchString = :searchString")
	void deleteSearchString(String searchString);
	
	@Query("select g from #{#entityName} g where g.isProcess = :isProcess and g.endPage = :endPage")
	List<SearchString> fetchStringActive(String isProcess, long endPage);
	
	@Transactional
	@Modifying
	@Query("update #{#entityName} g set g.endPage = :endPage, g.isProcess = :isProcess where g.searchString = :searchString")
	void updateSearchStringTotalPage(String searchString, long endPage, String isProcess);
}
