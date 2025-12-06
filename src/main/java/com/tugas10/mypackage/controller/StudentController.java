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
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping 
    public String listStudents(
        Model model, 
        @RequestParam(defaultValue = "") String keyword, 
        @RequestParam(defaultValue = "") String major,
        @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable 
    ) {
        Page<Student> studentPage = studentService.getStudents(keyword, major, pageable);
        
        List<String> uniqueMajors = studentService.getAllStudents().stream() 
                .map(Student::getMajor)
                .distinct()
                .collect(Collectors.toList());
        
        model.addAttribute("studentPage", studentPage); 
        model.addAttribute("students", studentPage.getContent()); 
        model.addAttribute("currentPage", studentPage.getNumber()); 
        model.addAttribute("totalPages", studentPage.getTotalPages()); 
        model.addAttribute("keyword", keyword); 
        model.addAttribute("major", major); 
        model.addAttribute("uniqueMajors", uniqueMajors); 
        
        return "students"; 
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("student", new Student());
        return "add-student";
    }

    @PostMapping("/add")
    public String saveStudent(Student student, RedirectAttributes redirectAttributes) {
        try {
            studentService.saveStudent(student);
            redirectAttributes.addFlashAttribute("message", "Data mahasiswa berhasil ditambahkan!");

            return "redirect:/students";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("student", student);

            return "redirect:/students/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Student student = studentService.getStudentById(id);
            model.addAttribute("student", student);
            return "edit-student";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/students";
        }
    }
    @PostMapping("/edit")
    public String updateStudent(Student student, RedirectAttributes redirectAttributes) {
        try {
            studentService.saveStudent(student);
            redirectAttributes.addFlashAttribute("message", "Data mahasiswa berhasil diubah!");

            return "redirect:/students";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("student", student);

            return "redirect:/students/edit/" + student.getId();
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("message", "Mahasiswa berhasil dihapus.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/students";
    }
}