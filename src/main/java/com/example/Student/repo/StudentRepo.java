package com.example.Student.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.Student.entity.Student;

@Repository
public interface StudentRepo extends JpaRepository<Student, Long> {
	List<Student> findByNameContainingOrEmailContainingOrCourseContaining(
            String name, String email, String course);
	
	Student findByName(String name);
	
	 @Query("SELECT COUNT(DISTINCT s.course) FROM Student s")
	    long countCourses();

	    @Query("SELECT DISTINCT s.course FROM Student s")
	    List<String> findDistinctCourses();
}
