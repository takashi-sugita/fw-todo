package com.example.todoapp;

import java.util.List;
import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TodoService {

    private final TodoMapper todoMapper;

    public TodoService(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    public List<Todo> search(String keyword, String category, String order) {
        return search(keyword, category, order, null, null);
    }

    public List<Todo> search(String keyword, String category, String order, LocalDate from, LocalDate to) {
        return todoMapper.search(keyword, category, order, from, to, true);
    }

    public List<Todo> searchForList(String keyword, String category, String order, boolean includeCompleted) {
        return todoMapper.search(keyword, category, order, null, null, includeCompleted);
    }

    public List<Todo> searchForList(String keyword, String category, String order, boolean includeCompleted,
            int page) {
        return todoMapper.searchPage(keyword, category, order, includeCompleted, 10, (page - 1) * 10);
    }

    public int countForList(String keyword, String category, boolean includeCompleted) {
        return todoMapper.countForList(keyword, category, includeCompleted);
    }

    public Todo findById(Long id) {
        return todoMapper.findById(id);
    }

    public int create(Todo todo) {
        int result = todoMapper.insert(todo);
        log.info("Todo created successfully");
        return result;
    }

    public int update(Todo todo) {
        int result = todoMapper.update(todo);
        log.info("Todo updated successfully");
        return result;
    }

    public int updatePinned(Long id, boolean pinned) {
        return todoMapper.updatePinnedById(id, pinned);
    }

    public int delete(Long id) {
        int result = todoMapper.deleteById(id);
        log.info("Todo deleted successfully");
        return result;
    }

    public List<Todo> searchDeleted() {
        return todoMapper.searchDeleted();
    }

    public int restore(Long id) {
        return todoMapper.restoreById(id);
    }
}
