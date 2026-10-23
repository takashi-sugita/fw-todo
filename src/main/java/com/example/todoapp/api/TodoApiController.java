package com.example.todoapp.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
