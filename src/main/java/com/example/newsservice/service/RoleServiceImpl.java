package com.example.newsservice.service;

import com.example.newsservice.dto.RoleDto;
import com.example.newsservice.repository.RoleRepository;
import com.example.newsservice.security.Role;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.util.StringUtils;

@Service
public class RoleServiceImpl implements RoleService {

    public static final String ROLE_PREFIX = "ROLE_";

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {this.roleRepository = roleRepository;}

    @Override
    public Role addRole(RoleDto roleDto) {
        return roleRepository.save(Role.builder().roleName(StringUtils.concat(ROLE_PREFIX, roleDto.getRoleName().toUpperCase())).build());
    }

    @Override
    public Role finRoleByRoleName(String roleName) {
        return roleRepository.findByRoleName(ROLE_PREFIX + roleName.toUpperCase()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found. Role name: " + roleName));
    }

    @Override
    public void deleteRole(Role role) {
        roleRepository.delete(role);
    }

    @Override
    public Role update(Role role) {
        role.setRoleName(ROLE_PREFIX + role.getRoleName().toUpperCase());
        return roleRepository.save(role);
    }
}
