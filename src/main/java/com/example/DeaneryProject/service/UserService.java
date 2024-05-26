package com.example.DeaneryProject.service;

import com.example.DeaneryProject.model.*;
import com.example.DeaneryProject.model.enums.WorkerType;
import com.example.DeaneryProject.repository.GroupRepository;
import com.example.DeaneryProject.repository.ImageRepository;
import com.example.DeaneryProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ImageRepository imageRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final GreetingMailService greetingMailService;

    @PreAuthorize("hasAuthority('ADMINISTRATOR_WORKER')")
    public String createWorker(Worker worker, User creator) throws IOException {
        String email = worker.getEmail();
        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            if (existingUser instanceof Worker) {
                log.info("A worker with this email already exists");
                return "A worker with this email already exists";
            } else {
                log.info("A user with this email already exists but is not a worker");
                return "A user with this email already exists but is not a worker";
            }
        }

        String firstName = worker.getFirstName();
        String lastName = worker.getLastName();
        String fatherName = worker.getFatherName();
        String password = worker.getPassword();
        String phoneNumber = worker.getPhoneNumber();

        Pattern namePattern = Pattern.compile("^[A-Za-z]+$");
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        Pattern passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{5,}$");
        Pattern phoneNumberPattern = Pattern.compile("^\\+380\\d{9}$");

        if (!namePattern.matcher(firstName).matches() || !namePattern.matcher(lastName).matches() || !namePattern.matcher(fatherName).matches()) {
            log.info("Names can only contain letters");
            return "Names can only contain letters";
        }

        if (!emailPattern.matcher(email).matches()) {
            log.info("Email is not valid");
            return "Email is not valid";
        }

        if (!passwordPattern.matcher(password).matches()) {
            log.info("Password must contain at least one letter, one number, one special character, and be at least 5 characters long");
            return "Password must contain at least one letter, one number, one special character, and be at least 5 characters long";
        }

        if (!phoneNumberPattern.matcher(phoneNumber).matches()) {
            log.info("Phone number must be in format +380XXXXXXXXXXX");
            return "Phone number must be in format +380XXXXXXXXXXX";
        }

        List<User> usersWithPhoneNumber = userRepository.findByPhoneNumber(phoneNumber);
        if (!usersWithPhoneNumber.isEmpty()) {
            log.info("Phone number is already taken");
            return "Phone number is already taken";
        }

        Image defaultImage = imageService.getDefaultImage();
        worker.setProfileImage(defaultImage);

        worker.setActive(true);
        worker.setPassword(passwordEncoder.encode(password));
        log.info("Creating worker: {}", worker);
        userRepository.save(worker);

        if (creator != null) {
            if (greetingMailService.sendGreetingMailToWorker(worker.getEmail(), password, creator.getEmail())) {
                return "Worker created successfully";
            } else {
                return "Worker created successfully, but email was not sent (check the email address domain). Recommended ro recreate the worker.";
            }
        }

        return "System worker created successfully";
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR_WORKER')")
    public String deleteWorker(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return "User not found";
        } else if (!(user instanceof Worker)) {
            return "User is not a worker";
        }

        if (((Worker) user).getWorkerType()== WorkerType.ADMINISTRATOR_WORKER) {
            return "You can't delete an administrator";
        }

        List<Group> groups = groupRepository.findAllByLastModifiedBy(user);
        for (Group group : groups) {
            group.setLastModifiedBy(null);
            groupRepository.save(group);
        }

        log.info("Deleting user, that is worker with id: {}", id);
        userRepository.delete(user);
        return "Worker deleted successfully";
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR_WORKER')")
    public String deleteMyProfile(User user) {
        if (user instanceof Worker && ((Worker) user).getWorkerType() == WorkerType.ADMINISTRATOR_WORKER) {
            long count = userRepository.countByWorkerType(WorkerType.ADMINISTRATOR_WORKER);
            if (count <= 1) {
                return "The last administrative worker cannot delete their own account";
            }
        }

        List<Group> groups = groupRepository.findAllByLastModifiedBy(user);

        for (Group group : groups) {
            group.setLastModifiedBy(null);
            groupRepository.save(group);
        }

        userRepository.delete(user);
        return "Profile deleted successfully";
    }

    @PreAuthorize("authentication.principal instanceof T(com.example.DeaneryProject.model.Worker)")
    public String createStudent(Student student, Group group, User creator) throws IOException {
        String email = student.getEmail();
        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            if (existingUser instanceof Student) {
                log.info("A student with this email already exists");
                return "A student with this email already exists";
            } else {
                log.info("A user with this email already exists but is not a student");
                return "A user with this email already exists but is not a student";
            }
        }

        String firstName = student.getFirstName();
        String lastName = student.getLastName();
        String fatherName = student.getFatherName();
        String password = student.getPassword();

        Pattern namePattern = Pattern.compile("^[A-Za-z]+$");
        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        Pattern passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{5,}$");

        if (!namePattern.matcher(firstName).matches() || !namePattern.matcher(lastName).matches() || !namePattern.matcher(fatherName).matches()) {
            log.info("Names can only contain letters");
            return "Names can only contain letters";
        }

        if (!emailPattern.matcher(email).matches()) {
            log.info("Email is not valid");
            return "Email is not valid";
        }

        if (!passwordPattern.matcher(password).matches()) {
            log.info("Password must contain at least one letter, one number, one special character, and be at least 5 characters long");
            return "Password must contain at least one letter, one number, one special character, and be at least 5 characters long";
        }

        Image defaultImage = imageService.getDefaultImage();
        student.setProfileImage(defaultImage);

        student.setActive(true);
        student.setPassword(passwordEncoder.encode(password));
        student.setGroup(group);
        log.info("Creating student: {}", student);
        userRepository.save(student);

        if (creator != null) {
            if (greetingMailService.sendGreetingMailToStudent(student.getEmail(), password, creator.getEmail())) {
                return "Student created successfully";
            } else {
                return "Student created successfully, but email was not sent (check the email address domain). Recommended ro recreate the student.";
            }
        }
        return "Student created successfully";
    }

    @PreAuthorize("authentication.principal instanceof T(com.example.DeaneryProject.model.Worker)")
    public String deleteStudent(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return "User not found";
        }
        if (user instanceof Student student) {
            Group group = student.getGroup();
            User groupLeader = group.getGroupLeader();
            if (groupLeader != null && groupLeader.equals(student)) {
                group.setGroupLeader(null);
                groupRepository.save(group);
            }
            group.getStudents().remove(student);
            userRepository.delete(student);
            log.info("Deleting user, that is student with id: {}", id);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();

            group.setLastModifiedBy(currentUser);
            group.setDateOfModification(LocalDateTime.now());
            groupRepository.save(group);

            return "Student deleted successfully";
        }
        return "User is not a student";
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> listUsers(String keyWord) {
        if (keyWord == null) {
            return userRepository.findAll();
        }
        return userRepository.findByKeyword(keyWord);
    }

    public List<User> listWorkers(String keyWord) {
        List<User> workers = new ArrayList<>();
        if (keyWord == null) {
            for (User user : userRepository.findAll()) {
                if (user instanceof Worker) {
                    workers.add(user);
                }
            }
        } else {
            for (User user : userRepository.findByKeyword(keyWord)) {
                if (user instanceof Worker) {
                    workers.add(user);
                }
            }
        }
        return workers;
    }

    public List<User> listStudents(String keyWord) {
        List<User> students = new ArrayList<>();
        if (keyWord == null) {
            for (User user : userRepository.findAll()) {
                if (user instanceof Student) {
                    students.add(user);
                }
            }
        } else {
            for (User user : userRepository.findByKeyword(keyWord)) {
                if (user instanceof Student) {
                    students.add(user);
                }
            }
        }
        return students;
    }

    public List<User> listStudentsInGroup(String keyWord, Group group) {
        List<User> students = new ArrayList<>();
        if (keyWord == null) {
            for (User user : userRepository.findAll()) {
                if (user instanceof Student && ((Student) user).getGroup().equals(group)) {
                    students.add(user);
                }
            }
        } else {
            for (User user : userRepository.findByKeyword(keyWord)) {
                if (user instanceof Student && ((Student) user).getGroup().equals(group)) {
                    students.add(user);
                }
            }
        }
        return students;
    }

    @PreAuthorize("authentication.principal instanceof T(com.example.DeaneryProject.model.Worker)")
    public String setStudentAsLeader(Long studentId, Long groupId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group != null) {
            for (Student student : group.getStudents()) {
                student.setLeader(false);
                userRepository.save(student);
            }
            Student student = (Student) userRepository.findById(studentId).orElse(null);
            if (student != null) {
                student.setLeader(true);
                userRepository.save(student);
                group.setGroupLeader(student);

                log.info("Updating group: {}", group);
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                User currentUser = (User) authentication.getPrincipal();

                group.setLastModifiedBy(currentUser);
                group.setDateOfModification(LocalDateTime.now());
                groupRepository.save(group);
                return "Student set as leader successfully";
            }
        }
        return "Group or student not found";
    }

    public String changePassword(User user, String currentPassword, String newPassword, String confirmPassword) {
        Pattern passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{5,}$");

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.info("Current password is incorrect");
            return "Current password is incorrect";
        }

        if (!newPassword.equals(confirmPassword)) {
            log.info("New password does not match the confirm password");
            return "New password does not match the confirm password";
        }

        if (!passwordPattern.matcher(newPassword).matches()) {
            log.info("Password must contain at least one letter, one number, one special character, and be at least 5 characters long");
            return "Password must contain at least one letter, one number, one special character, and be at least 5 characters long";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password changed successfully";
    }

    public String setProfileImage(User user, MultipartFile file) throws IOException {
        if (file.getSize() != 0) {
            Image oldImage = user.getProfileImage();

            log.info("Setting profile image for user: {}", user);
            Image newImage = new Image();
            newImage.setBytes(file.getBytes());
            newImage.setContentType(file.getContentType());
            newImage.setSize(file.getSize());
            newImage.setOriginalFileName(file.getOriginalFilename());
            user.setProfileImage(newImage);

            userRepository.save(user);

            if (oldImage != null) {
                imageRepository.delete(oldImage);
            }
            return "Profile image set successfully";
        }
        return "No image selected";
    }

    public String updateUserInfo(User currentUser, String firstName, String lastName, String fatherName) {
        Pattern namePattern = Pattern.compile("^[A-Za-z]+$");

        if (currentUser != null) {
            if (firstName.isEmpty() && lastName.isEmpty() && fatherName.isEmpty()) {
                return "No information to change";
            }

            if (!firstName.isEmpty() && namePattern.matcher(firstName).matches()) {
                currentUser.setFirstName(firstName);
                log.info("Updating user's first name to: {}", firstName);
            } else if (!firstName.isEmpty()) {
                log.info("First name is not valid");
                return "First name is not valid";
            }
            if (!lastName.isEmpty() && namePattern.matcher(lastName).matches()) {
                currentUser.setLastName(lastName);
                log.info("Updating user's last name to: {}", lastName);
            } else if (!lastName.isEmpty()) {
                log.info("Last name is not valid");
                return "Last name is not valid";
            }
            if (!fatherName.isEmpty() && namePattern.matcher(fatherName).matches()) {
                currentUser.setFatherName(fatherName);
                log.info("Updating user's father name to: {}", fatherName);
            } else if (!fatherName.isEmpty()) {
                log.info("Father name is not valid");
                return "Father name is not valid";
            }
            userRepository.save(currentUser);
            return "User info updated successfully";
        }
        return "User not found";
    }
}