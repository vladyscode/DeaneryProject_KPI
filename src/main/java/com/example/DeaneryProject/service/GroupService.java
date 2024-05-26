package com.example.DeaneryProject.service;

import com.example.DeaneryProject.model.Group;
import com.example.DeaneryProject.model.User;
import com.example.DeaneryProject.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    public List<Group> listGroup(String name) {
        if (name == null) {
            return groupRepository.findAll();
        }
        return groupRepository.findByKeyWord(name);
    }

    @PreAuthorize("authentication.principal instanceof T(com.example.DeaneryProject.model.Worker)")
    public String createGroup(Group group) {
        if (!groupRepository.findByName(group.getName()).isEmpty()) {
            log.info("Group with this name already exists");
            return "Group with this name already exists";
        }
        String groupName = group.getName();
        Pattern pattern = Pattern.compile("^[A-Z]{2}-\\d{2}$"); // FOR KPI!!!
        Matcher matcher = pattern.matcher(groupName);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        group.setLastModifiedBy(currentUser);

        if (matcher.matches()) {
            log.info("Creating group: {}", group);
            groupRepository.save(group);
            return "Group created successfully";
        } else {
            log.info("Group name is incorrect");
            return "Group name is incorrect";
        }
    }

    public void updateGroup(Group group) {
        log.info("Updating group: {}", group);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        group.setLastModifiedBy(currentUser);
        group.setDateOfModification(LocalDateTime.now());
        groupRepository.save(group);
    }

    @PreAuthorize("authentication.principal instanceof T(com.example.DeaneryProject.model.Worker)")
    public String deleteGroup(Long id) {
        log.info("Deleting group with id: {}", id);
        groupRepository.deleteById(id);
        return "Group deleted successfully";
    }

    public Group getGroupById(Long id) {
        return groupRepository.findById(id).orElse(null);
    }
}
