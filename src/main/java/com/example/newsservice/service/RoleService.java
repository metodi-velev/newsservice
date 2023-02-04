package com.example.newsservice.service;

import com.example.newsservice.dto.NewsDto;
import com.example.newsservice.dto.ReadStatusDto;
import com.example.newsservice.dto.RoleDto;
import com.example.newsservice.entity.News;
import com.example.newsservice.entity.Photo;
import com.example.newsservice.security.Role;
import com.example.newsservice.security.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleService {

    public Role addRole(RoleDto roleDto);

    Role finRoleByRoleName(String roleName);

    void deleteRole(Role role);

    Role update(Role role);
}
