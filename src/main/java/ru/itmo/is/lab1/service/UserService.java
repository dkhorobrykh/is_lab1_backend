package ru.itmo.is.lab1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.is.lab1.exception.CustomException;
import ru.itmo.is.lab1.exception.ExceptionEnum;
import ru.itmo.is.lab1.model.AdminRequest;
import ru.itmo.is.lab1.model.User;
import ru.itmo.is.lab1.repository.AdminRequestRepository;
import ru.itmo.is.lab1.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class UserService {
    @Inject
    UserRepository userRepository;

    @Inject
    private JwtProvider jwtProvider;

    @Inject
    private RoleService roleService;

    @Inject
    private AdminRequestRepository adminRequestRepository;

    public User getByUsername(String login) {
        return userRepository.findByUsernameIgnoreCase(login)
                .orElseThrow(() -> new CustomException(ExceptionEnum.USER_NOT_FOUND));
    }

    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionEnum.USER_NOT_FOUND));
    }

    @Transactional
    public String register(String username, String password) {
        if (userRepository.findByUsernameIgnoreCase(username).isPresent())
            throw new CustomException(ExceptionEnum.USER_ALREADY_EXISTED);

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user = userRepository.save(user);

        return jwtProvider.generateAccessToken(user);
    }

    public String login(String username, String password) {
        var userOpt = userRepository.findByUsernameIgnoreCase(username);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password))
            throw new CustomException(ExceptionEnum.WRONG_CREDENTIALS);

        return jwtProvider.generateAccessToken(userOpt.get());
    }

    public void makeAdminRequest(SecurityContext securityContext) {
        User currentUser = roleService.getCurrentUser(securityContext);

        Optional<AdminRequest> currentActiveAdminRequest = adminRequestRepository.findActiveAdminRequestByUserId(currentUser.getId());

        if (currentActiveAdminRequest.isPresent())
            throw new CustomException(ExceptionEnum.ACTIVE_ADMIN_REQUEST_ALREADY_EXIST);

        AdminRequest request = AdminRequest.builder()

                .user(currentUser)
                .createdDatetime(Instant.now())
                .approved(false)

                .build();

        adminRequestRepository.save(request);
    }

    public List<AdminRequest> getAllActiveAdminRequests() {
        return adminRequestRepository.findAllOrderByCreatedDatetime();
    }

    public AdminRequest getAdminRequestById(Long requestId) {
        return adminRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException(ExceptionEnum.ADMIN_REQUEST_NOT_FOUND));
    }

    public void approveAdminRequest(Long requestId, SecurityContext securityContext) {
        AdminRequest adminRequest = getAdminRequestById(requestId);

        if (!adminRequest.isActive())
            throw new CustomException(ExceptionEnum.ADMIN_REQUEST_IS_NOT_ACTIVE);

        User user = adminRequest.getUser();

        user.setAdmin(true);
        adminRequest.setActive(false);
        adminRequest.setApproved(true);

        adminRequestRepository.save(adminRequest);
        userRepository.save(user);

        log.info("User with id [{}] approved admin request from user with id [{}]", roleService.getCurrentUser(securityContext), user.getId());
    }

    public void declineAdminRequest(Long requestId, SecurityContext securityContext) {
        AdminRequest adminRequest = getAdminRequestById(requestId);

        if (!adminRequest.isActive())
            throw new CustomException(ExceptionEnum.ADMIN_REQUEST_IS_NOT_ACTIVE);

        User user = adminRequest.getUser();

        user.setAdmin(false);
        adminRequest.setActive(false);
        adminRequest.setApproved(false);

        adminRequestRepository.save(adminRequest);
        userRepository.save(user);

        log.info("User with id [{}] declined admin request from user with id [{}]", roleService.getCurrentUser(securityContext).getId(), user.getId());
    }
}
