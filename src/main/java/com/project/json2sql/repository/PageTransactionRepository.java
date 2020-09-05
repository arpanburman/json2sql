package com.project.json2sql.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.project.json2sql.model.PageTransaction;

public interface PageTransactionRepository extends JpaRepository<PageTransaction, Long>{

	@Query("select count(*) from #{#entityName} g where g.searchString = :searchString and g.isProcess =:isProcess and g.pageNumber = :page and g.maxResult = :maxResult")
	int fetchByActiveSearchString(String searchString, String isProcess, long page, long maxResult);
	
	@Query("select count(*) from #{#entityName} g where g.searchString = :searchString and g.pageNumber = :page and g.maxResult = :maxResult")
	int fetchByNotAvailableSearchString(String searchString, long page, long maxResult);
	
	@Query("select g from #{#entityName} g where g.searchString = :searchString and g.isProcess =:isProcess")
	List<PageTransaction> fetchByTransSearchString(String searchString, String isProcess);

	@Transactional
	@Modifying
	@Query("delete from #{#entityName} g where g.searchString = :searchString and g.pageNumber = :page and g.maxResult = :maxResult")
	void deletePageTrans(String searchString, long page, long maxResult);
	
	@Query("select g from #{#entityName} g where g.isProcess =:isProcess")
	List<PageTransaction> fetchByTransFlag(String isProcess);
	
	@Transactional
	@Modifying
	@Query("delete from #{#entityName} g where g.id = :id")
	void deleteById(long id);

	@Query("select count(*) from #{#entityName} g")
	int countTransactionDetails();
}
