package com.tugas10.mypackage.services;

import com.tugas10.mypackage.model.Student;
import com.tugas10.mypackage.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Autowired 
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Page<Student> getStudents(String keyword, String majorFilter, Pageable pageable) {
        
        String searchKeyword = (keyword != null) ? keyword : "";
        String filterMajor = (majorFilter != null) ? majorFilter : "";
        
        if (!searchKeyword.isEmpty() && !filterMajor.isEmpty()) {
            return studentRepository.findByNameContainingAndMajorContaining(searchKeyword, filterMajor, pageable);
            
        } else if (!searchKeyword.isEmpty()) {
            return studentRepository.findByNameContaining(searchKeyword, pageable);
            
        } else if (!filterMajor.isEmpty()) {
            return studentRepository.findByMajorContaining(filterMajor, pageable);
            
        } else {
            return studentRepository.findAll(pageable);
        }
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mahasiswa tidak ditemukan dengan ID: " + id));
    }
    
    @Override
    public Student saveStudent(Student student) {
        
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama mahasiswa tidak boleh kosong.");
        }
        if (student.getRegistrationYear() == null || student.getRegistrationYear() < 1900) {
             throw new IllegalArgumentException("Tahun masuk tidak valid.");
        }
        
        if (student.getEmail() == null || student.getEmail().trim().isEmpty() || !student.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email tidak boleh kosong dan harus berformat valid (@...).");
        }

        if (studentRepository.existsByEmail(student.getEmail())) {
            
            if (student.getId() == null) {
                throw new IllegalArgumentException("Email sudah terdaftar. Email harus unik.");
            }
            
            Optional<Student> existingStudentOptional = studentRepository.findById(student.getId());
            
            if (existingStudentOptional.isPresent()) {
                 String oldEmail = existingStudentOptional.get().getEmail();
                 
                 if (!oldEmail.equals(student.getEmail()) && studentRepository.existsByEmail(student.getEmail())) {
                    throw new IllegalArgumentException("Email sudah digunakan oleh mahasiswa lain. Email harus unik.");
                 }
            }
        }

        return studentRepository.save(student); 
    }
    
    @Override
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
             throw new IllegalArgumentException("Mahasiswa tidak ditemukan untuk dihapus dengan ID: " + id);
        }
        studentRepository.deleteById(id);
    }
}