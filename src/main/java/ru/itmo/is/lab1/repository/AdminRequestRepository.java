package ru.itmo.is.lab1.repository;

import ru.itmo.is.lab1.model.AdminRequest;
import ru.itmo.is.lab1.model.User;

import java.util.List;
import java.util.Optional;

public class AdminRequestRepository extends AbstractRepository<AdminRequest, Long> {
    public AdminRequestRepository() {
        super(AdminRequest.class);
    }

    public List<AdminRequest> findAllOrderByCreatedDatetime() {
        return entityManager
                .createQuery("SELECT ar FROM AdminRequest ar WHERE ar.active is true ORDER BY ar.createdDatetime DESC", AdminRequest.class)
                .getResultStream()
                .toList();
    }

    public Optional<AdminRequest> findActiveAdminRequestByUserId(Long userId) {
        return entityManager
                .createQuery("SELECT ar FROM AdminRequest ar WHERE ar.active is true AND ar.user.id = :userId", AdminRequest.class)
                .setParameter("userId", userId)
                .getResultStream()
                .findFirst();
    }
}
