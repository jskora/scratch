package org.song.example;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;


public class StudyGroupTest {

	private List<Student> testStudents;

	@Before
	public void testStudyGroupClass() {
		// prepare the data for test
		testStudents = new ArrayList<Student>();
		testStudents.add(new Student(1, "Student No.1", 60));
		testStudents.add(new Student(2, "Student No.2", 70));
		testStudents.add(new Student(3, "Student No.2", 80));
	}

	@Test
	public void testStudyGroup() {
		// Start the unit test
		final StudyGroup testGroup = new StudyGroup();
		for(Student student: testStudents) {
			testGroup.addStudent(student);
		}
		assertEquals(testGroup.getGroupSize(), testStudents.size());
		
		Student testStudent = testStudents.get(0);
		Student returnedStudent = testGroup.getStudent(testStudent.getId());
		assertSame(returnedStudent, testStudent);
		assertEquals(returnedStudent.getId(), testStudent.getId());
		assertEquals(returnedStudent.getName(), testStudent.getName());
		assertEquals(returnedStudent.getScore(), testStudent.getScore());
		
		testGroup.removeStudent(testStudent.getId());
		assertEquals(testGroup.getGroupSize(), testStudents.size() - 1);
		
		testGroup.clear();
		assertEquals(testGroup.getGroupSize(), 0);
	}
}
