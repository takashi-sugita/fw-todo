package com.example.todoapp.api;

import java.util.List;
import java.util.Map;
import java.net.URI;
import java.time.LocalDate;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
            @RequestParam(name = "order", required = false) String order,
            @RequestParam(name = "from", required = false) LocalDate from,
            @RequestParam(name = "to", required = false) LocalDate to) {
        return todoService.search(keyword, category, order, from, to).stream()
                .map(TodoDto::from)
                .toList();
    }

    @GetMapping(value = "/api/todos.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportTodosCsv(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "category", defaultValue = "") String category,
            @RequestParam(name = "order", defaultValue = "asc") String order,
            @RequestParam(name = "includeCompleted", defaultValue = "false") boolean includeCompleted) {
        if (!"desc".equals(order)) {
            order = "asc";
        }
        List<Todo> todos = todoService.searchForList(keyword, category, order, includeCompleted);
        String rows = todos.stream().map(todo -> String.join(",",
                csvCell(todo.getTitle()),
                csvCell(todo.getCategory()),
                csvCell(todo.getPriority() == 1 ? "高" : (todo.getPriority() == 2 ? "中" : "低")),
                csvCell(todo.getDueDate() == null ? "" : todo.getDueDate().toString()),
                csvCell(Boolean.TRUE.equals(todo.getCompleted())
                        ? "完了" + (todo.getCompletedAt() == null ? "" : "（" + todo.getCompletedAt().toLocalDate() + "）")
                        : "未完了"),
                csvCell(todo.getDetail())))
                .collect(Collectors.joining("\r\n"));
        String csv = "やること,ジャンル,優先度,期限,状態,メモ\r\n" + rows + "\r\n";
        byte[] body = ("\uFEFF" + csv).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"todos.csv\"")
                .body(body);
    }

    private String csvCell(String value) {
        String safeValue = value == null ? "" : value;
        if (safeValue.startsWith("=") || safeValue.startsWith("+")) {
            safeValue = "'" + safeValue;
        }
        return "\"" + safeValue.replace("\"", "\"\"") + "\"";
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
