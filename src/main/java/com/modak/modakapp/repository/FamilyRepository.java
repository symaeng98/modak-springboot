package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Family;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamilyRepository extends JpaRepository<Family,Integer> {

}
