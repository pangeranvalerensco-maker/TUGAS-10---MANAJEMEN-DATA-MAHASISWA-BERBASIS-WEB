package com.tugas10.mypackage.services;

import com.tugas10.mypackage.model.Student;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface StudentService {
    
    Student saveStudent(Student student);
    
    Page<Student> getStudents(String keyword, String majorFilter, Pageable pageable);

    List<Student> getAllStudents();

    Student getStudentById(Long id); 

    void deleteStudent(Long id); 
}