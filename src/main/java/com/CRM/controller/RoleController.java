package com.CRM.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CRM.constant.Enpoint;
import com.CRM.request.Role.createRoleRequest;
import com.CRM.service.Role.RoleService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping(Enpoint.Role.BASE)
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseEntity<?> getAllRole(@RequestParam(defaultValue = "0") int page,
            @RequestParam int limit,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(roleService.getAllRoles(page, limit, sortBy, direction));
    }

    @GetMapping(Enpoint.Role.ID)
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PostMapping(Enpoint.Role.CREATE)
    public ResponseEntity<?> createRole(@RequestBody createRoleRequest createRoleRequest) {
        return ResponseEntity.ok(roleService.createRole(createRoleRequest));
    }
}