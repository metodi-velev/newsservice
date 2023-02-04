package com.example.newsservice.controller;

import com.example.newsservice.dto.RoleDto;
import com.example.newsservice.entity.News;
import com.example.newsservice.security.Role;
import com.example.newsservice.service.RoleService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/role")
@PreAuthorize("hasRole('ADMIN')")
@Api(description = "APIs for roles service")
public class RoleController {

    private static final Logger LOG = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<Role> addRole(@Valid @RequestBody RoleDto role) {
        Role addedRole = roleService.addRole(role);
        LOG.info("Created Role with name: {} and title : {}", addedRole.getId(), addedRole.getRoleName());
        return new ResponseEntity<>(addedRole, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "{roleName}", produces = "application/json")
    public ResponseEntity<News> deleteRole(@PathVariable(value = "roleName") String roleName) {
        Role role = roleService.finRoleByRoleName(roleName);
        LOG.info("Deleted Role with name : {}", role.getRoleName());
        roleService.deleteRole(role);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("{roleName}")
    public ResponseEntity<Role> update(@PathVariable(value = "roleName") String roleName,
                                       @Valid @RequestBody RoleDto roleDto) {
        Role role = roleService.finRoleByRoleName(roleName);
        role.setRoleName(roleDto.getRoleName());
        LOG.info("Updating Role with name : {}", role.getRoleName());
        Role updatedRole = roleService.update(role);
        return new ResponseEntity<>(updatedRole, HttpStatus.OK);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<List> badReqeustHandler(ConstraintViolationException e) {
        List<String> errors = new ArrayList<>(e.getConstraintViolations().size());

        e.getConstraintViolations().forEach(constraintViolation -> {
            errors.add(constraintViolation.getPropertyPath().toString() + " : " + constraintViolation.getMessage());
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
