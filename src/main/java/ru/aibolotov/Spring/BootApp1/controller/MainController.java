package ru.aibolotov.Spring.BootApp1.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.aibolotov.Spring.BootApp1.dao.UserDao;
import ru.aibolotov.Spring.BootApp1.model.Role;
import ru.aibolotov.Spring.BootApp1.model.User;
import ru.aibolotov.Spring.BootApp1.repository.RoleRepository;
import ru.aibolotov.Spring.BootApp1.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin(origins = "*")
public class MainController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private UserDao userDao;

    static final String USER_LIST = "/list";
    static final String USER_FORM = "/user-form";
    static final String USER_DATA = "/userdata";

    @JsonView(User.class)
    @RequestMapping(value = USER_LIST, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> getUsersList()
    {
        return new ResponseEntity<List<User>>(userDao.getListUsers(), HttpStatus.OK);
    }

    @RequestMapping(value = "/update")
    public String update(@RequestParam("userId") long id, Model model) {
        if (id == -1) {
            User user = new User();
            model.addAttribute("user", user);
            return USER_FORM;
        }
        List<Role> roles = roleRepository.findAll();
        User user = userRepository.getOne(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return USER_FORM;
    }

    @RequestMapping(value = "/saveUser")
    public String saveUser(@RequestParam("name") String name, @RequestParam("password") String password, @RequestParam("roles") Set<Role> role) {
        if (userRepository.findAllByName(name) != null) {
            User user = userRepository.findAllByName(name);
            user.setName(name);
            user.setPassword(password);
            user.setRoles(role);
            userRepository.save(user);
        } else {
            User user = new User(name, password, role);
            userRepository.save(user);
        }
        return "redirect:/mainPage";
    }

    @RequestMapping(value = "/delete")
    public String delete(@RequestParam("id") long id, Model model) {
        userRepository.deleteById(id);
        return "redirect:/mainPage";
    }

    @RequestMapping(value = "/addUser")
    public String addUser(@RequestParam("userId") long id, Model model) {
        Set<Role> userRoles = new HashSet<>(roleRepository.findAll());
        List<User> userList = userRepository.findAll();
        int i = userList.size();
        Long idLast = userList.get(i - 1).getId();
        User user = new User();
        user.setId(idLast + 1);
        user.setName("");
        user.setPassword("");
        user.setRoles(userRoles);

        model.addAttribute("user", user);
        model.addAttribute("roles", userRoles);
        return USER_FORM;
    }

    @RequestMapping(value = "/userdata")
    public String userdata(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findAllByName(auth.getName());
        model.addAttribute("user", user);
        return USER_DATA;
    }


}
