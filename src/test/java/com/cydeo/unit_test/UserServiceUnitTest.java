package com.cydeo.unit_test;

import com.cydeo.dto.RoleDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Role;
import com.cydeo.entity.User;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.KeycloakService;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    // 1. need to use Mockito to mock objects
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private TaskService taskService;
    @Mock
    private KeycloakService keycloakService;
    @InjectMocks
    private UserServiceImpl userService;
    @Spy
    private UserMapper userMapper=new UserMapper(new ModelMapper()); //why new obj?

    User user;
    UserDTO userDTO;

    @BeforeEach
    void setUp(){

        user=new User();
        user.setId(1L);
        user.setFirstName("John"); //not necessary
        user.setLastName("Doe"); //not necessary
        user.setUserName("user");
        user.setPassWord("Abc1");
        user.setEnabled(true);
        user.setRole(new Role("Manager"));

        userDTO=new UserDTO();
        userDTO.setId(1L);
        userDTO.setFirstName("John"); //not necessary
        userDTO.setLastName("Doe"); //not necessary
        userDTO.setUserName("user");
        userDTO.setPassWord("Abc1");
        userDTO.setEnabled(true);

        RoleDTO roleDTO=new RoleDTO();
        roleDTO.setDescription("Manager");

        userDTO.setRole(roleDTO);

    }
    private List<User> getUsers(){
        User user2=new User();
        user2.setId(2L);
        user2.setFirstName("Emily");
        return List.of(user, user2);
    }
    private List<UserDTO> getUserDTOs(){
        UserDTO userDTO2=new UserDTO();
        userDTO2.setId(2L);
        userDTO2.setFirstName("Emily");
        return List.of(userDTO, userDTO2); //unsupported Operation error, we can not sort because we can not change
    }
    @Test
    void should_list_all_users(){

        //stub, when this method is call, then return list
        when(userRepository.findAllByIsDeletedOrderByFirstNameDesc(false)).thenReturn(getUsers());
        //create expected list, and I am expecting to receive userDTO list
        List<UserDTO> expectedList=getUserDTOs();
        //expectedList.sort(Comparator.comparing(UserDTO::getFirstName).reversed()); //sort in Desc order
        //actual list come from userService
        List<UserDTO> actualList=userService.listAllUsers();
        //compare lists actual and expected
        //assertEquals(expectedList,actualList);b- gives error - we will use assertJ
        //assertThat(actualList).isEqualTo(expectedList);
        // we do not need to verify
        //if we compare fields of objects, use usingRecursiveComparison
        //assertThat(actualList).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedList);
        //if too many nulls lets do this:
        assertThat(actualList).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedList);
    }





}
