package com.example.todoapp.api;

import java.util.List;
import java.util.Map;
import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.BindingResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import com.example.todoapp.Todo;
import com.example.todoapp.TodoService;

@RestController
public class TodoApiController {

    private final TodoService todoService;

    public TodoApiController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/api/todos")
    public List<TodoDto> getTodos(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "order", required = false) String order) {
        return todoService.search(keyword, category, order).stream()
                .map(TodoDto::from)
                .toList();
    }

    @GetMapping("/api/todos/{id}")
    public ResponseEntity<?> getTodo(@PathVariable Long id) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            return notFound(id);
        }
        return ResponseEntity.ok(TodoDto.from(todo));
    }

    @PostMapping("/api/todos")
    public ResponseEntity<?> createTodo(@Valid @RequestBody TodoRequest request,
            BindingResult bindingResult, HttpServletRequest httpRequest) {
        if (bindingResult.hasErrors()) {
            return badRequest(bindingResult, httpRequest);
        }
        Todo todo = request.toTodo();
        if (todo.getCompleted() == null) {
            todo.setCompleted(false);
        }
        todoService.create(todo);
        Todo created = todoService.findById(todo.getId());
        return ResponseEntity.created(URI.create("/api/todos/" + todo.getId()))
                .body(TodoDto.from(created));
    }

    @PutMapping("/api/todos/{id}")
    public ResponseEntity<?> replaceTodo(@PathVariable Long id, @Valid @RequestBody TodoRequest request,
            BindingResult bindingResult, HttpServletRequest httpRequest) {
        if (bindingResult.hasErrors()) {
            return badRequest(bindingResult, httpRequest);
        }
        if (todoService.findById(id) == null) {
            return notFound(id);
        }
        Todo todo = request.toTodo();
        todo.setId(id);
        todoService.update(todo);
        return ResponseEntity.ok(TodoDto.from(todoService.findById(id)));
    }

    @DeleteMapping("/api/todos/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long id) {
        if (todoService.findById(id) == null) {
            return notFound(id);
        }
        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<ProblemDetail> notFound(Long id) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, "指定したIDのTodoは見つかりませんでした。");
        problem.setTitle("Not Found");
        problem.setInstance(URI.create("/api/todos/" + id));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    private ResponseEntity<ProblemDetail> badRequest(BindingResult bindingResult,
            HttpServletRequest httpRequest) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "入力に誤りがあります");
        problem.setTitle("Bad Request");
        problem.setInstance(URI.create(httpRequest.getRequestURI()));
        List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
                .map(error -> Map.of("field", error.getField(), "message", error.getDefaultMessage()))
                .toList();
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }
}
