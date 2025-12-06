package com.tugas10.mypackage.controller;

import com.tugas10.mypackage.model.Student;
import com.tugas10.mypackage.services.StudentService;

import java.util.stream.Collectors;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/students") // Base path untuk semua mapping di controller ini
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // --- ROUTE 1: LIST MAHASISWA (READ) - Dengan Search, Filter, Pagination ---
    // Route: /students (GET)
    @GetMapping 
    public String listStudents(
        Model model, 
        @RequestParam(defaultValue = "") String keyword, 
        @RequestParam(defaultValue = "") String major,
        // Default size 5, diurutkan berdasarkan createdAt dari yang terbaru (DESC)
        @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable 
    ) {
        // 1. Panggil Service Layer untuk mendapatkan data yang sudah di-page/search/filter
        Page<Student> studentPage = studentService.getStudents(keyword, major, pageable);
        
        // 2. Ambil list jurusan unik DARI SEMUA data (menggunakan method yang baru dikembalikan)
        //    Ini digunakan untuk mengisi dropdown Filter.
        List<String> uniqueMajors = studentService.getAllStudents().stream() // Tidak merah lagi!
                .map(Student::getMajor)
                .distinct()
                .collect(Collectors.toList());
        
        // 3. Tambahkan atribut ke Model
        model.addAttribute("studentPage", studentPage); 
        model.addAttribute("students", studentPage.getContent()); 
        model.addAttribute("currentPage", studentPage.getNumber()); 
        model.addAttribute("totalPages", studentPage.getTotalPages()); 
        model.addAttribute("keyword", keyword); 
        model.addAttribute("major", major); 
        model.addAttribute("uniqueMajors", uniqueMajors); 
        
        return "students"; 
    }

    // --- ROUTE 2: FORM TAMBAH DATA (GET) ---
    // Route: /students/add (GET)
    @GetMapping("/add")
    public String showAddForm(Model model) {
        // Objek Student kosong dikirim ke form untuk menampung input baru
        model.addAttribute("student", new Student());
        return "add-student"; // Memanggil view: add-student.html
    }

    // --- ROUTE 3: SIMPAN DATA (POST) ---
    // Route: /students/add (POST)
    @PostMapping("/add")
    public String saveStudent(Student student, RedirectAttributes redirectAttributes) {
        try {
            // Panggil Service Layer untuk menyimpan (disinilah validasi berjalan)
            studentService.saveStudent(student);
            redirectAttributes.addFlashAttribute("message", "Data mahasiswa berhasil ditambahkan!");

            // Redirect ke halaman list setelah sukses
            return "redirect:/students";
        } catch (IllegalArgumentException e) {
            // Tangani exception validasi dari Service Layer
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            // Kirim kembali objek 'student' yang gagal (agar input tidak hilang)
            redirectAttributes.addFlashAttribute("student", student);

            // Redirect ke form tambah lagi jika gagal (menggunakan GET)
            return "redirect:/students/add";
        }
    }

    // ... (lanjutan kode StudentController.java) ...

    // --- ROUTE 4: FORM EDIT DATA (GET) ---
    // Route: /students/edit/{id} (GET)
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Ambil data mahasiswa berdasarkan ID
            Student student = studentService.getStudentById(id);
            model.addAttribute("student", student);
            return "edit-student"; // Memanggil view: edit-student.html
        } catch (IllegalArgumentException e) {
            // Tangani exception jika ID tidak ditemukan
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/students";
        }
    }

    // --- ROUTE 5: UPDATE DATA (POST) ---
    // Route: /students/edit (POST)
    @PostMapping("/edit")
    public String updateStudent(Student student, RedirectAttributes redirectAttributes) {
        try {
            // Panggil Service Layer untuk update (validasi sama seperti save)
            studentService.saveStudent(student);
            redirectAttributes.addFlashAttribute("message", "Data mahasiswa berhasil diubah!");

            return "redirect:/students";
        } catch (IllegalArgumentException e) {
            // Tangani exception validasi dari Service Layer
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            // Kirim kembali ID dan data yang gagal agar user tetap di halaman edit
            redirectAttributes.addFlashAttribute("student", student);

            // Redirect ke form edit lagi jika gagal
            return "redirect:/students/edit/" + student.getId();
        }
    }

    // --- ROUTE 6: HAPUS DATA (DELETE) ---
    // Route: /students/delete/{id} (GET atau POST)
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // 1. Panggil fungsi hapus dari Service Layer
            studentService.deleteStudent(id);
            // 2. Tambahkan pesan sukses
            redirectAttributes.addFlashAttribute("message", "Mahasiswa berhasil dihapus.");
        } catch (IllegalArgumentException e) {
            // 3. Tangani exception dari Service (jika ID tidak ditemukan)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // 4. Redirect kembali ke halaman list mahasiswa
        return "redirect:/students";
    }
}