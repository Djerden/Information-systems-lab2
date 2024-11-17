package com.djeno.backend_lab1.service;

import com.djeno.backend_lab1.models.AdminRequest;
import com.djeno.backend_lab1.models.enums.Role;
import com.djeno.backend_lab1.repositories.AdminRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRequestService {
    private final UserService userService;
    private final AdminRequestRepository adminRequestRepository;

    // Создание заявки
    public void createAdminRequest() {
        var currentUser = userService.getCurrentUser();

        // Проверяем, есть ли уже администратор в системе
        boolean hasAdmin = userService.existsByRole(Role.ROLE_ADMIN);

        if (!hasAdmin) {
            // Если администратора нет, сразу назначаем роль ADMIN
            currentUser.setRole(Role.ROLE_ADMIN);
            userService.save(currentUser);
        } else {
            // Если администратор есть, создаем заявку со статусом PENDING
            AdminRequest request = AdminRequest.builder()
                    .user(currentUser)
                    .status("PENDING")
                    .build();
            adminRequestRepository.save(request);
        }
    }

    // Получение всех заявок
    public List<AdminRequest> getAllRequests() {
        return adminRequestRepository.findAll();
    }

    // Одобрение заявки
    public void approveRequest(Long requestId) {
        AdminRequest request = adminRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("APPROVED");
        adminRequestRepository.save(request);

        // Назначаем пользователю роль ADMIN
        var user = request.getUser();
        user.setRole(Role.ROLE_ADMIN);
        userService.save(user);
    }

    // Отклонение заявки
    public void rejectRequest(Long requestId) {
        AdminRequest request = adminRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("REJECTED");
        adminRequestRepository.save(request);
    }
}
