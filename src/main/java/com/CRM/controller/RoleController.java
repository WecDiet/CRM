package com.CRM.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CRM.constant.Enpoint;
import com.CRM.request.Role.createRoleRequest;
import com.CRM.request.Role.updateRoleRequest;
import com.CRM.service.Role.RoleService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping(Enpoint.Role.BASE)
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseEntity<?> getAllRole(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(roleService.getAllRoles(page, limit, sortBy, direction));
    }

    @GetMapping(Enpoint.Role.ID)
    public ResponseEntity<?> getRoleById(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PostMapping(Enpoint.Role.CREATE)
    public ResponseEntity<?> createRole(@RequestBody createRoleRequest createRoleRequest) {
        return ResponseEntity.ok(roleService.createRole(createRoleRequest));
    }

    @PutMapping(Enpoint.Role.UPDATE)
    public ResponseEntity<?> updateRole(@PathVariable UUID id, @RequestBody updateRoleRequest updateRoleRequest) {
        return ResponseEntity.ok(roleService.updateRole(id, updateRoleRequest));
    }

    @DeleteMapping(Enpoint.Role.DELETE)
    public ResponseEntity<?> deleteRole(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.deleteRole(id));
    }

    @GetMapping(Enpoint.Role.TRASH)
    public ResponseEntity<?> getAllRoleTrash(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(roleService.getAllRoleTrash(page, limit, sortBy, direction));
    }

}