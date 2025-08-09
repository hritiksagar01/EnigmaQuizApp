package com.sagar.question_service.service;

import com.sagar.question_service.Dao.QuestionDao;
import com.sagar.question_service.Dao.UserDao;
import com.sagar.question_service.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao qd;

    @Autowired
    private UserDao ud;

    public List<Question> getAllQuestions() {
        return qd.findAll();
    }

    public String addQuestion(Question question) {
        qd.save(question);
        return "success";
    }

    public String deleteStudent(String uniqueId) {
        Optional<Student> student = ud.findByUniqueId(uniqueId);
        if (student.isPresent()) {
            ud.deleteByUniqueId(uniqueId);
            return "Student with unique ID " + uniqueId + " has been deleted successfully.";
        } else {
            return "Student with unique ID " + uniqueId + " not found.";
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<QuestionWrapper>> getQuestionFromId(List<Integer> questionId) {
        List<QuestionWrapper> wrappers = new ArrayList<>();
        List<Question> questions = qd.findAllById(questionId);

        for (Question q : questions) {
            QuestionWrapper qr = new QuestionWrapper();
            qr.setId(q.getId());
            qr.setQuestion_title(q.getQuestion_title());
            qr.setOption1(q.getOption1());
            qr.setOption2(q.getOption2());
            qr.setOption3(q.getOption3());
            qr.setOption4(q.getOption4());
            wrappers.add(qr);
        }

        return new ResponseEntity<>(wrappers, HttpStatus.OK);
    }

    public ResponseEntity<Integer> getScore(List<Response> responses) {
        int right = 0;
        for (Response response : responses) {
            Optional<Question> question = qd.findById(response.getId());
            if (question.isPresent()) {
                if (response.getResponse().equals(question.get().getRight_answer())) {
                    right++;
                } else {
                    right--;
                }
            }
        }
        return new ResponseEntity<>(right, HttpStatus.OK);
    }

    public boolean isAuthorizedUser(Student student) {
        Optional<Student> user = ud.findByUniqueIdAndName(student.getUniqueId().trim(), student.getName().trim());
        return user.isPresent();
    }

    public void save(Student student) {
        ud.save(student);
    }

    public ResponseEntity<Integer> getScoreAndUpdateUser(List<Response> responses, String uniqueId) {
        int right = 0;
        for (Response response : responses) {
            Optional<Question> question = qd.findById(response.getId());
            if (question.isPresent()) {
                if (response.getResponse().equals(question.get().getRight_answer())) {
                    right++;
                } else {
                    right--;
                }
            }
        }
        Optional<Student> existingStudent = ud.findByUniqueId(uniqueId);
        if (existingStudent.isPresent()) {
            Student student = existingStudent.get();
            student.setScore(right);
            ud.save(student);

        }
        return new ResponseEntity<>(right, HttpStatus.OK);

    }

    public List<Question> updateQuestions(List<QuestionUpdateDTO> updateDTOList) {
        List<Question> updatedQuestions = new ArrayList<>();

        for (QuestionUpdateDTO updateDTO : updateDTOList) {
            Optional<Question> questionOptional = qd.findById(updateDTO.getId());

            if (questionOptional.isPresent()) {
                Question question = questionOptional.get();

                question.setQuestion_title(updateDTO.getQuestionTitle());
                question.setOption1(updateDTO.getOption1());
                question.setOption2(updateDTO.getOption2());
                question.setOption3(updateDTO.getOption3());
                question.setOption4(updateDTO.getOption4());
                question.setRight_answer(updateDTO.getRightAnswer());

                Question updatedQuestion = qd.save(question);
                updatedQuestions.add(updatedQuestion);
            }
        }

        return updatedQuestions;
    }

    public Map<String, String> getTwoOptions(int questionId) {
        Optional<Question> optionalQuestion = qd.findById(questionId);

        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            String correctOption = question.getRight_answer();

            List<String> allOptions = Arrays.asList(
                    question.getOption1(),
                    question.getOption2(),
                    question.getOption3(),
                    question.getOption4()
            );

            List<String> incorrectOptions = allOptions.stream()
                    .filter(option -> !option.equals(correctOption))
                    .toList();

            String incorrectOption = incorrectOptions.get(new Random().nextInt(incorrectOptions.size()));

            Map<String, String> options = new HashMap<>();
            options.put("correct", correctOption);
            options.put("incorrect", incorrectOption);

            return options;
        } else {
            throw new NoSuchElementException("Question not found");
        }
    }

    public void resetAllScores() {
        List<Student> students = ud.findAll();

        for (Student student : students) {
            student.setScore(0);
        }

        ud.saveAll(students);
    }

    public List<Question> updateOrAddQuestions(List<QuestionUpdateDTO> updateDTOList) {
        List<Question> updatedQuestions = new ArrayList<>();

        if (qd.count() == 0) {
            for (QuestionUpdateDTO updateDTO : updateDTOList) {
                Question newQuestion = new Question();
                newQuestion.setQuestion_title(updateDTO.getQuestionTitle());
                newQuestion.setOption1(updateDTO.getOption1());
                newQuestion.setOption2(updateDTO.getOption2());
                newQuestion.setOption3(updateDTO.getOption3());
                newQuestion.setOption4(updateDTO.getOption4());
                newQuestion.setRight_answer(updateDTO.getRightAnswer());
                updatedQuestions.add(qd.save(newQuestion));
            }
        } else {
            for (QuestionUpdateDTO updateDTO : updateDTOList) {
                Optional<Question> existingQuestion = qd.findById(updateDTO.getId());

                if (existingQuestion.isPresent()) {
                    Question question = existingQuestion.get();
                    question.setQuestion_title(updateDTO.getQuestionTitle());
                    question.setOption1(updateDTO.getOption1());
                    question.setOption2(updateDTO.getOption2());
                    question.setOption3(updateDTO.getOption3());
                    question.setOption4(updateDTO.getOption4());
                    question.setRight_answer(updateDTO.getRightAnswer());
                    updatedQuestions.add(qd.save(question));
                } else {
                    Question newQuestion = getQuestion(updateDTO);
                    updatedQuestions.add(qd.save(newQuestion));
                }
            }
        }

        return updatedQuestions;
    }

    private static Question getQuestion(QuestionUpdateDTO updateDTO) {
        Question newQuestion = new Question();
        newQuestion.setQuestion_title(updateDTO.getQuestionTitle());
        newQuestion.setOption1(updateDTO.getOption1());
        newQuestion.setOption2(updateDTO.getOption2());
        newQuestion.setOption3(updateDTO.getOption3());
        newQuestion.setOption4(updateDTO.getOption4());
        newQuestion.setRight_answer(updateDTO.getRightAnswer());
        return newQuestion;
    }

    @Transactional
    public void truncateAllQuestions() {
        qd.deleteAll();
    }
    @Transactional
    public void truncateAllStudents() {
        ud.deleteAll();
    }
    @Transactional

    public void resetPlayedStatus() {
        ud.resetPlayedStatusForAllStudents();
    }
}
