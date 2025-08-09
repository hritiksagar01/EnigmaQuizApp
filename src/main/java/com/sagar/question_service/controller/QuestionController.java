package com.sagar.question_service.controller;
import com.sagar.question_service.Dao.UserDao;
import com.sagar.question_service.model.*;
import com.sagar.question_service.service.QuestionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")

@RequestMapping("/question")

public class QuestionController {
    @Autowired
    QuestionService qs;

    @Autowired
     UserDao studentRepository;

    @GetMapping("/allQuestions")
    public List<Question> getAllQuestion() {
        return qs.getAllQuestions();
    }

    @PostMapping("/add")
    public String addQuestion(@RequestBody Question question) {
        return qs.addQuestion(question);
    }

    @PostMapping("getQuestions")
    public ResponseEntity<List<QuestionWrapper>> getQuestionFromId(@RequestBody List<Integer> questionId) {
        return qs.getQuestionFromId(questionId);
    }

    @PostMapping("/getScore")
    public ResponseEntity<Integer> getScore(@RequestBody List<Response> response) {
        return qs.getScore(response);
    }

    @PostMapping("/validateUser")
    public ResponseEntity<Map<String, String>> validateUser(@RequestBody Student student) {
        boolean isAuthorized = qs.isAuthorizedUser(student);
        Map<String, String> response = new HashMap<>();

        if (isAuthorized) {
            response.put("message", "User is authorized!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Invalid credentials. Please retry.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    // Get two options for a lifeline (hint)
    @GetMapping("/lifeline/{questionId}")
    public ResponseEntity<Map<String, String>> getTwoOptions(@PathVariable() int questionId) {
        try {
            Map<String, String> options = qs.getTwoOptions(questionId);
            return ResponseEntity.ok(options);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @Transactional
    @DeleteMapping("/delete/{uniqueId}")
    public ResponseEntity<String> deleteStudent(@PathVariable String uniqueId) {
        String response = qs.deleteStudent(uniqueId);
        if (response.contains("deleted")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerStudent(@RequestBody Student student) {
        studentRepository.save(student);
        return new ResponseEntity<>("Student registered successfully", HttpStatus.CREATED);
    }

    @PostMapping("/getScoreAndUpdate")
    public ResponseEntity<Integer> getScoreAndUpdate(@RequestBody TotalResponse totalResponse) {
        System.out.println("Received responseList: " + totalResponse.getResponseList());
        System.out.println("Received uniqueId: " + totalResponse.getUniqueId());
        ResponseEntity<Integer> scoreResponse = qs.getScoreAndUpdateUser(totalResponse.getResponseList(), totalResponse.getUniqueId());
        studentRepository.findByUniqueId(totalResponse.getUniqueId()).ifPresent(student -> {
            student.setPlayed(true);
            studentRepository.save(student);
        });
        return scoreResponse;
    }
    @PutMapping("/update-questions")
    public ResponseEntity<List<Question>> updateQuestions(@RequestBody List<QuestionUpdateDTO> updateDTOList) {
        List<Question> updatedQuestions = qs.updateOrAddQuestions(updateDTOList);
        return ResponseEntity.ok(updatedQuestions);
    }
    @PutMapping("/reset-scores")
    public ResponseEntity<String> resetAllScores() {
        qs.resetAllScores();
        return new ResponseEntity<>("All students' scores have been reset to 0.", HttpStatus.OK);
    }
    @DeleteMapping("/truncate-questions")
    public ResponseEntity<String> truncateQuestions() {
        qs.truncateAllQuestions();
        return new ResponseEntity<>("All questions have been truncated.", HttpStatus.OK);
    }
    @PutMapping("/truncate-students")
    public ResponseEntity<String> truncateAllStudents() {
        qs.truncateAllStudents();
        return new ResponseEntity<>("All students have been deleted successfully.", HttpStatus.OK);
    }
    @PutMapping("/reset-played")
    public ResponseEntity<String> resetPlayedStatus() {
        qs.resetPlayedStatus();
        return new ResponseEntity<>("The 'played' status for all students has been reset.", HttpStatus.OK);
    }
}