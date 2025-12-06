package com.tugas10.mypackage.services;

import com.tugas10.mypackage.model.Student;
import com.tugas10.mypackage.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service // Menandakan bahwa ini adalah komponen Service Spring
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    // Dependency Injection: Spring akan secara otomatis menyediakan instance StudentRepository
    @Autowired 
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // --- READ FUNCTIONALITY ---
    @Override
    public Page<Student> getStudents(String keyword, String majorFilter, Pageable pageable) {
        
        // Atur default jika input null
        String searchKeyword = (keyword != null) ? keyword : "";
        String filterMajor = (majorFilter != null) ? majorFilter : "";
        
        // Logika sederhana untuk memanggil query yang paling sesuai
        if (!searchKeyword.isEmpty() && !filterMajor.isEmpty()) {
            // Jika ada keyword dan filter major
            return studentRepository.findByNameContainingAndMajorContaining(searchKeyword, filterMajor, pageable);
            
        } else if (!searchKeyword.isEmpty()) {
            // Hanya berdasarkan keyword
            return studentRepository.findByNameContaining(searchKeyword, pageable);
            
        } else if (!filterMajor.isEmpty()) {
            // Hanya berdasarkan major
            return studentRepository.findByMajorContaining(filterMajor, pageable);
            
        } else {
            // Tampilkan semua data (default)
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
    
    // --- CREATE & UPDATE FUNCTIONALITY ---
    @Override
    public Student saveStudent(Student student) {
        
        // 1. Validasi Nama dan Tahun Masuk (Logika sebelum menyimpan/mengupdate) [cite: 35]
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama mahasiswa tidak boleh kosong.");
        }
        if (student.getRegistrationYear() == null || student.getRegistrationYear() < 1900) {
             throw new IllegalArgumentException("Tahun masuk tidak valid.");
        }
        
        // 2. Validasi Email Tidak Boleh Kosong dan Format (@...) [cite: 34]
        if (student.getEmail() == null || student.getEmail().trim().isEmpty() || !student.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email tidak boleh kosong dan harus berformat valid (@...).");
        }

        // 3. Validasi Email Unik [cite: 51]
        if (studentRepository.existsByEmail(student.getEmail())) {
            
            // a. Jika ID null (operasi CREATE), maka email sudah pasti duplikat
            if (student.getId() == null) {
                throw new IllegalArgumentException("Email sudah terdaftar. Email harus unik.");
            }
            
            // b. Jika ID ada (operasi UPDATE), cek apakah email yang di-input
            //    berbeda dengan email lama (yang ada di DB) DAN email yang baru sudah dipakai orang lain.
            Optional<Student> existingStudentOptional = studentRepository.findById(student.getId());
            
            if (existingStudentOptional.isPresent()) {
                 String oldEmail = existingStudentOptional.get().getEmail();
                 
                 // Jika email BEDA dengan yang lama DAN email yang baru sudah ada di DB
                 if (!oldEmail.equals(student.getEmail()) && studentRepository.existsByEmail(student.getEmail())) {
                    throw new IllegalArgumentException("Email sudah digunakan oleh mahasiswa lain. Email harus unik.");
                 }
            }
        }

        return studentRepository.save(student); // Simpan/Update data [cite: 27, 30]
    }
    
    // --- DELETE FUNCTIONALITY ---
    @Override
    public void deleteStudent(Long id) {
        // Cek dulu apakah ID ada sebelum dihapus [cite: 58]
        if (!studentRepository.existsById(id)) {
             throw new IllegalArgumentException("Mahasiswa tidak ditemukan untuk dihapus dengan ID: " + id);
        }
        studentRepository.deleteById(id);
    }
}