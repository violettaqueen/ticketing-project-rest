package com.cydeo.service;


import com.cydeo.dto.UserDTO;
import com.cydeo.exception.TicketingProjectException;

import java.util.List;

public interface UserService  {

    UserDTO findByUserName(String username) throws TicketingProjectException;
    List<UserDTO> listAllUsers();
    UserDTO save(UserDTO user);
//    void deleteByUserName(String username);
    UserDTO update(UserDTO user) throws TicketingProjectException;
    void delete(String username) throws TicketingProjectException;
    List<UserDTO> listAllByRole(String role);

}
