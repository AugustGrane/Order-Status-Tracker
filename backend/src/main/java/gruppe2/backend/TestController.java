package gruppe2.backend;

import gruppe2.backend.model.Test;
import gruppe2.backend.model.Test.TestStatus;
import gruppe2.backend.repository.TestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
public class TestController {

    @Autowired
    private TestRepository testRepository;

    @GetMapping("/tests")
    public ResponseEntity<List<Test>> getAllTests(@RequestParam(required = false) String testName) {
        try {
            List<Test> tests = new ArrayList<>();

            if (testName == null)
                tests.addAll(testRepository.findAll());
            else
                tests.addAll(testRepository.findByTestNameContaining(testName));

            if (tests.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(tests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tests/status/{status}")
    public ResponseEntity<List<Test>> getTestsByStatus(@PathVariable("status") TestStatus status) {
        try {
            List<Test> tests = testRepository.findByStatus(status);

            if (tests.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(tests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tests/{id}")
    public ResponseEntity<Test> getTestById(@PathVariable("id") long id) {
        Optional<Test> testData = testRepository.findById(id);

        return testData.map(test -> new ResponseEntity<>(test, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/tests")
    public ResponseEntity<Test> createTest(@RequestBody Test test) {
        try {
            Test _test = testRepository.save(new Test(
                    test.getTestName(),
                    test.getDescription()));
            return new ResponseEntity<>(_test, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/tests/{id}")
    public ResponseEntity<Test> updateTest(@PathVariable("id") long id, @RequestBody Test test) {
        Optional<Test> testData = testRepository.findById(id);

        if (testData.isPresent()) {
            Test _test = testData.get();
            _test.setTestName(test.getTestName());
            _test.setDescription(test.getDescription());
            _test.setStatus(test.getStatus());
            _test.setResult(test.getResult());
            return new ResponseEntity<>(testRepository.save(_test), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/tests/{id}/status")
    public ResponseEntity<Test> updateTestStatus(
            @PathVariable("id") long id,
            @RequestParam TestStatus status,
            @RequestParam(required = false) String result) {
        Optional<Test> testData = testRepository.findById(id);

        if (testData.isPresent()) {
            Test _test = testData.get();
            _test.setStatus(status);
            if (result != null) {
                _test.setResult(result);
            }
            return new ResponseEntity<>(testRepository.save(_test), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/tests/{id}")
    public ResponseEntity<HttpStatus> deleteTest(@PathVariable("id") long id) {
        try {
            testRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/tests")
    public ResponseEntity<HttpStatus> deleteAllTests() {
        try {
            testRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}