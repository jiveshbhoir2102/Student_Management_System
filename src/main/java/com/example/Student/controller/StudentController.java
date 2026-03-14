package com.example.Student.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.Student.entity.Student;
import com.example.Student.repo.StudentRepo;

import jakarta.servlet.http.HttpSession;

@Controller
public class StudentController {

    @Autowired
    StudentRepo srepo;

    // ================= LOGIN PAGE =================

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // ================= LOGIN LOGIC =================

    @PostMapping("/login")
    public String login(@RequestParam String role,
                        @RequestParam String name,
                        HttpSession session,
                        Model model) {

        // ADMIN LOGIN
        if(role.equals("admin")) {

            if(name.equals("admin")) {
                session.setAttribute("admin", "admin");
                return "redirect:/dashboard";
            }
        }

        // STUDENT LOGIN
        if(role.equals("student")) {

            Student s = srepo.findByName(name);

            if(s != null) {
                session.setAttribute("student", s);
                return "redirect:/studentDashboard";
            }
        }

        model.addAttribute("msg", "Invalid Login");
        return "login";
    }

    // ================= ADMIN DASHBOARD =================

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        if(session.getAttribute("admin") == null) {
            return "redirect:/login";
        }

        model.addAttribute("students", srepo.findAll());

        long courseCount = srepo.countCourses();
        model.addAttribute("courseCount", courseCount);

        return "dashboard";
    }

    // ================= VIEW STUDENTS =================

    @GetMapping("/viewStudents")
    public String viewStudents(@RequestParam(defaultValue="0") int page,
                               HttpSession session,
                               Model model) {

        if(session.getAttribute("admin") == null) {
            return "redirect:/login";
        }

        int size = 5;

        Page<Student> studpage = srepo.findAll(PageRequest.of(page, size));

        model.addAttribute("students", studpage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studpage.getTotalPages());

        return "view";
    }

    // ================= ADD STUDENT =================

    @GetMapping("/add")
    public String addStudentPage(HttpSession session) {

        if(session.getAttribute("admin") == null) {
            return "redirect:/login";
        }

        return "add";
    }

    @PostMapping("/saveStudent")
    public String saveStudent(Student s) {

        srepo.save(s);
        return "redirect:/viewStudents";
    }

    // ================= EDIT =================

    @GetMapping("/edit/{id}")
    public String editStudent(@PathVariable long id,
                              HttpSession session,
                              Model model) {

        if(session.getAttribute("admin") == null) {
            return "redirect:/login";
        }

        Student s = srepo.findById(id).get();
        model.addAttribute("student", s);

        return "edit";
    }

    @PostMapping("/updateStudent")
    public String updateStudent(Student s) {

        srepo.save(s);
        return "redirect:/viewStudents";
    }

    // ================= DELETE =================

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable long id,
                                HttpSession session) {

        if(session.getAttribute("admin") == null) {
            return "redirect:/login";
        }

        srepo.deleteById(id);

        return "redirect:/viewStudents";
    }

    // ================= SEARCH =================

    @GetMapping("/search")
    public String searchStudent(@RequestParam String keyword,
                                HttpSession session,
                                Model model) {

        if(session.getAttribute("admin") == null) {
            return "redirect:/login";
        }

        List<Student> list =
                srepo.findByNameContainingOrEmailContainingOrCourseContaining(
                        keyword, keyword, keyword);

        model.addAttribute("students", list);
        
        
        model.addAttribute("search", true); 

        return "view";
    }
    
    
    // ===================== Courses =================//
    
    @GetMapping("/courses")
    public String showCourses(Model model){

        List<String> courses = srepo.findDistinctCourses();

        model.addAttribute("courses", courses);
        model.addAttribute("courseCount", courses.size());

        return "courses";
    }

    // ================= STUDENT DASHBOARD =================

    @GetMapping("/studentDashboard")
    public String studentDashboard(HttpSession session, Model model) {

        Student s = (Student) session.getAttribute("student");

        if(s == null) {
            return "redirect:/login";
        }

        model.addAttribute("student", s);

        return "student-dashboard";
    }

    // ================= STUDENT PROFILE =================

    @GetMapping("/studentProfile")
    public String studentProfile(HttpSession session, Model model) {

        Student s = (Student) session.getAttribute("student");

        if(s == null) {
            return "redirect:/login";
        }

        model.addAttribute("student", s);

        return "student-profile";
    }

    // ================= LOGOUT =================

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }

    // ================= HOME =================

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

}