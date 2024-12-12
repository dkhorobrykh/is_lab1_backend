package ru.itmo.is.lab1.repository;

import jakarta.enterprise.context.ApplicationScoped;
import ru.itmo.is.lab1.model.AuditLog;
import ru.itmo.is.lab1.model.ImportHistory;

import java.util.List;

@ApplicationScoped
public class ImportRepository extends AbstractRepository<ImportHistory, Long> {
    public ImportRepository() {
        super(ImportHistory.class);
    }

    public List<ImportHistory> findAll() {
        return entityManager.createQuery(
                        "SELECT ih FROM ImportHistory ih ORDER BY ih.id DESC", ImportHistory.class)
                .getResultStream()
                .toList();
    }

    public List<ImportHistory> findAllByUser_Id(Long userId) {
        return entityManager.createQuery(
                        "SELECT ih FROM ImportHistory ih WHERE ih.user.id = :userId ORDER BY ih.id DESC", ImportHistory.class)
                .setParameter("userId", userId)
                .getResultStream()
                .toList();
    }
}
