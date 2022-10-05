package org.outofoffice.eida;

import org.outofoffice.common.socket.EidaSocketClient;
import org.outofoffice.eida.application.EnrollmentService;
import org.outofoffice.eida.application.MajorService;
import org.outofoffice.eida.application.StudentService;
import org.outofoffice.eida.application.SubjectService;
import org.outofoffice.eida.domain.*;
import org.outofoffice.lib.context.EidaContext;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

public class Main {

    private static final AtomicLong SEQ = new AtomicLong(1L);
    private static final Scanner scanner = new Scanner(System.in);

    private static final MajorRepository majorRepository;
    private static final MajorService majorService;

    private static final SubjectRepository subjectRepository;
    private static final SubjectService subjectService;

    private static final StudentRepository studentRepository;
    private static final StudentService studentService;

    private static final EnrollmentRepository enrolmentRepository;
    private static final EnrollmentService enrolmentService;

    static {
        EidaContext.init(Main.class, new EidaSocketClient());

        majorRepository = (MajorRepository) EidaContext.getRepository(Major.class);
        majorService = new MajorService(majorRepository);

        subjectRepository = (SubjectRepository) EidaContext.getRepository(Subject.class);
        subjectService = new SubjectService(majorService, subjectRepository);

        studentRepository = (StudentRepository) EidaContext.getRepository(Student.class);
        studentService = new StudentService(majorService, studentRepository);

        enrolmentRepository = (EnrollmentRepository) EidaContext.getRepository(Enrollment.class);
        enrolmentService = new EnrollmentService(subjectService, studentService, enrolmentRepository);
    }


    public static void main(String[] args) {
        while (true) {
            System.out.println("############################");
            System.out.println("수행할 액션을 선택하세요(종료:-1)");
            System.out.println("1. 전공등록, 2. 과목등록, 3. 학생등록, 4. 수강신청등록, 5. 수강신청내역(학생), 6. 수강신청내역(과목)");
            System.out.print("=>");
            int i = scanner.nextInt();
            if (i == -1) break;
            scanner.nextLine();

            MAP.get(i).run();
        }
    }


    private static final Runnable onSelectMajorRegister = () -> {
        System.out.print("전공이름을 입력하세요:");
        String majorName = scanner.nextLine();

        System.out.print("영어이름을 입력하세요:");
        String mEnglishName = scanner.nextLine();

        majorService.save(majorName, mEnglishName);

        Major major = majorService.mustFind(majorName);
        System.out.println(major);
    };

    private static final Runnable onSelectSubject = () -> {
        System.out.println("전공을 선택하세요");
        List<Major> majors = majorService.findAll();
        Set<String> majorNames = majors.stream().map(Major::getMajorName).collect(Collectors.toSet());
        majorNames.add("교양");
        System.out.println("=>" +  String.join("\t", majorNames));

        String selectedMajor = "";
        while (!majorNames.contains(selectedMajor)) {
            if (!selectedMajor.equals("")) System.out.println("올바르지 않은 전공입니다.");
            selectedMajor = scanner.nextLine();
        }

        System.out.print("과목이름을 입력하세요:");
        String subjectName = scanner.nextLine();

        System.out.print("영어이름을 입력하세요:");
        String sEnglishName = scanner.nextLine();

        if(selectedMajor.equals("교양")) {
            subjectService.save(subjectName, sEnglishName);
        } else {
            subjectService.save(subjectName, sEnglishName, selectedMajor);
        }

        Subject subject = subjectService.mustFind(subjectName);
        System.out.println(subject);
    };

    private static final Runnable onSelectStudent = () -> {
        System.out.println("전공을 선택하세요");
        List<Major> majors = majorService.findAll();
        Set<String> majorNames = majors.stream().map(Major::getMajorName).collect(Collectors.toSet());
        System.out.println("=>" + String.join("\t", majorNames));

        String selectedMajor = "";
        while (!majorNames.contains(selectedMajor)) {
            if (!selectedMajor.equals("")) System.out.println("올바르지 않은 전공입니다");
            selectedMajor = scanner.nextLine();
        }

        System.out.println("이름을 입력하세요");
        String name = scanner.nextLine();

        System.out.println("생년월일을 입력하세요");
        String birth = scanner.nextLine();

        System.out.println("성별을 입력하세요");
        String gender = scanner.nextLine();

        String studentCode = String.format("%4s%02d%04d", LocalDateTime.now().getYear(), selectedMajor.hashCode() % 100, SEQ.getAndIncrement());

        studentService.save(studentCode, name, birth, gender, selectedMajor);
        Student student = studentService.mustFind(studentCode);
        System.out.println("student : " + student);
    };

    private static final Runnable onEnroll = () -> {
        System.out.println("학생을 선택하세요");
        List<Student> students = studentService.findAll();

        Map<String, Student> studentMap = students.stream().collect(toMap(Student::getStudentCode, identity()));
        List<String> codes = studentMap.keySet().stream().sorted().collect(toList());
        for (int i = 0; i < studentMap.size(); i++) {
            String code = codes.get(i);
            Student student = studentMap.get(code);
            String format = String.format("%d: %s(%s)", i + 1, student.getName(), code);
            System.out.println(format);
        }

        System.out.print("로그인할 학생을 선택하세요:");
        int sel = scanner.nextInt() - 1;
        scanner.nextLine();
        String code = codes.get(sel);
        Student student = studentMap.get(code);

        String major = student.getMajor().getMajorName();

        List<Subject> subjectsInMajor = subjectService.findAllByMajorName(major);
        List<Subject> subjectsInElectives = subjectService.findAllElectives();

        Set<String> subjectNames = Stream.concat(subjectsInMajor.stream(), subjectsInElectives.stream())
            .map(Subject::getSubjectName).collect(toSet());
        System.out.println("수강할 과목을 선택해주세요.\n" + String.join( ", ", subjectNames));
        String selectedSubject = "";

        while (!subjectNames.contains(selectedSubject)) {
            if (!selectedSubject.equals("")) System.out.println("올바르지 않은 과목입니다.");
            selectedSubject = scanner.nextLine();
        }

        enrolmentService.insert(selectedSubject, code);
        Enrollment enrolment = enrolmentService.mustFind(code, selectedSubject);
        System.out.println(enrolment);
    };


    private static final Runnable onEnrollShowByStudent = () -> {
        System.out.println("학생을 선택하세요");
        List<Student> students = studentService.findAll();

        Map<String, Student> studentMap = students.stream().collect(toMap(Student::getStudentCode, identity()));
        List<String> codes = studentMap.keySet().stream().sorted().collect(toList());
        for (int i = 0; i < studentMap.size(); i++) {
            String code = codes.get(i);
            Student student = studentMap.get(code);
            String format = String.format("%d: %s(%s)", i + 1, student.getName(), code);
            System.out.println(format);
        }

        System.out.print("로그인할 학생을 선택하세요:");
        int sel = scanner.nextInt() - 1;
        scanner.nextLine();
        String code = codes.get(sel);

        List<Enrollment> enrolments = enrolmentService.findByStudent(code);
        List<Subject> subjects = enrolments.stream().map(Enrollment::getSubject).collect(toList());
        subjects.forEach(System.out::println);
    };

    private static final Runnable onEnrollShowBySubject = () -> {
        System.out.println("과목을 선택하세요");
        List<Subject> subjects = subjectService.findAll();
        Set<String> subjectNames = subjects.stream().map(Subject::getSubjectName).collect(toSet());
        System.out.println("조회할 과목을 선택해주세요.\n" + String.join( ", ", subjectNames));
        String selectedSubject = "";

        while (!subjectNames.contains(selectedSubject)) {
            if (!selectedSubject.equals("")) System.out.println("올바르지 않은 과목입니다.");
            selectedSubject = scanner.nextLine();
        }

        List<Enrollment> enrolments = enrolmentService.findBySubject(selectedSubject);
        List<Student> students = enrolments.stream().map(Enrollment::getStudent).collect(toList());
        students.forEach(System.out::println);
    };


    private static final Map<Integer, Runnable> MAP = Map.of(
        1, onSelectMajorRegister,
        2, onSelectSubject,
        3, onSelectStudent,
        4, onEnroll,
        5, onEnrollShowByStudent,
        6, onEnrollShowBySubject
    );

}
