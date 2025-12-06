package com.tugas10.mypackage.repository; 

import com.tugas10.mypackage.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByEmail(String email);

    Page<Student> findByNameContainingAndMajorContaining(String name, String major, Pageable pageable);
    
    // Jika hanya perlu berdasarkan Nama (dan mengabaikan Jurusan)
    Page<Student> findByNameContaining(String name, Pageable pageable);
    
    // Jika hanya perlu berdasarkan Jurusan (dan mengabaikan Nama)
    Page<Student> findByMajorContaining(String major, Pageable pageable);
}