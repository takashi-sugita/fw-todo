package com.example.todoapp;

import java.util.List;
import java.time.LocalDate;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TodoMapper {

    List<Todo> search(@Param("keyword") String keyword, @Param("category") String category,
            @Param("order") String order, @Param("from") LocalDate from, @Param("to") LocalDate to,
            @Param("includeCompleted") boolean includeCompleted);

    List<Todo> searchPage(@Param("keyword") String keyword, @Param("category") String category,
            @Param("order") String order, @Param("includeCompleted") boolean includeCompleted,
            @Param("limit") int limit, @Param("offset") int offset);

    List<Todo> searchDeleted();

    int countForList(@Param("keyword") String keyword, @Param("category") String category,
            @Param("includeCompleted") boolean includeCompleted);

    Todo findById(Long id);

    int update(Todo todo);

    int updatePinnedById(@Param("id") Long id, @Param("pinned") boolean pinned);

    int insert(Todo todo);

    int deleteById(Long id);

    int restoreById(Long id);
}
