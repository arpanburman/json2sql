package com.project.json2sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.project.json2sql.model.OwnerProcess;

public interface OwnerProcessRepository extends CrudRepository<OwnerProcess, Long>{

}
