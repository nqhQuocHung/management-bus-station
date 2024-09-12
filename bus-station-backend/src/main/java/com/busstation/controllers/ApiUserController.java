package com.busstation.controllers;

import com.busstation.dtos.TicketDTO;
import com.busstation.dtos.UserChatDTO;
import com.busstation.dtos.UserDTO;
import com.busstation.pojo.User;
import com.busstation.services.TicketService;
import com.busstation.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jfr.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/users")
public class ApiUserController {
    @Autowired
    private UserService userService;

    @Autowired
    private TicketService ticketService;


    @PatchMapping(path = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserDTO> partialUpdate(@PathVariable Long id,
                                                 @ModelAttribute UserDTO payload,
                                                 @RequestParam(required = false) MultipartFile file
                                                 ) throws IOException, IllegalAccessException {
       return ResponseEntity.ok(userService.updateUser(id, payload, file));
    }


    @GetMapping("/self")
    public ResponseEntity<UserDTO> getSelfInformation() {
        return ResponseEntity.ok(userService.getAuthenticatedUser());
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserDTO>> listActiveUsers() {
        List<UserDTO> users = userService.listActiveUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/role/{id}")
    public ResponseEntity<List<UserDTO>> findActiveUsersByRoleId(@PathVariable Long id) {
        List<UserDTO> users = userService.findActiveUsersByRoleId(id);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/self/tickets")
    public ResponseEntity<List<TicketDTO>> retrieveTickets() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(ticketService.getTicketByUserId(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserChatDTO> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserChatDTO userChatDTO = UserChatDTO.builder()
                    .username((user.getUsername()))
                    .avatar(user.getAvatar())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .build();
            return ResponseEntity.ok(userChatDTO);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

