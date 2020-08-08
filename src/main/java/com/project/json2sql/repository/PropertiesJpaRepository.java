package com.project.json2sql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.json2sql.model.Properties;

@Repository
public interface PropertiesJpaRepository extends JpaRepository<Properties, Long>{

}
