package com.project.json2sql.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.project.json2sql.model.PageSkipTransaction;

public interface PageSkipTransactionRepository extends JpaRepository<PageSkipTransaction, Long>{

	@Query("select count(*) from #{#entityName} g where g.searchString = :searchString and g.isProcess=:isProcess and g.pageNumber = :page")
	int fetchByActiveSearchString(String searchString, String isProcess, int page);

	@Query("select g from #{#entityName} g where g.isProcess = :isProcess")
	List<PageSkipTransaction> fetchBySkipString(String isProcess);
	
	@Transactional
	@Modifying
	@Query("delete from #{#entityName} g where g.searchString = :searchString")
	void deleteSearchString(String searchString);
	
	@Transactional
	@Modifying
	@Query("delete from #{#entityName} g where g.searchString = :searchString and g.pageNumber = :pageNumber")
	void deleteSearchStringPage(String searchString, long pageNumber);
	
	@Query("select count(*) from #{#entityName} g where g.searchString = :searchString")
	int countSkipSearchString(String searchString);

}
