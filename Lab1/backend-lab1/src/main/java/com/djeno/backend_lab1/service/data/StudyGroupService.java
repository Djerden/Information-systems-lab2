package com.djeno.backend_lab1.service.data;

import com.djeno.backend_lab1.DTO.StudyGroupDTO;
import com.djeno.backend_lab1.exceptions.AccessDeniedException;
import com.djeno.backend_lab1.models.*;
import com.djeno.backend_lab1.models.enums.FormOfEducation;
import com.djeno.backend_lab1.models.enums.Role;
import com.djeno.backend_lab1.models.enums.Semester;
import com.djeno.backend_lab1.repositories.CoordinatesRepository;
import com.djeno.backend_lab1.repositories.PersonRepository;
import com.djeno.backend_lab1.repositories.StudyGroupHistoryRepository;
import com.djeno.backend_lab1.repositories.StudyGroupRepository;
import com.djeno.backend_lab1.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final StudyGroupHistoryRepository historyRepository;

    private final PersonService personService;
    private final UserService userService;

    public StudyGroup createStudyGroup(StudyGroupDTO studyGroupDTO) {
        var currentUser = userService.getCurrentUser();

        // Извлечение Coordinates по id из DTO
        Coordinates coordinates = coordinatesRepository.findById(studyGroupDTO.getCoordinatesId())
                .orElseThrow(() -> new RuntimeException("Coordinates not found"));

        // Извлечение Admin по id из DTO (если указан)
        Person admin = null;
        if (studyGroupDTO.getGroupAdminId() != null) {
            System.out.println(studyGroupDTO.getGroupAdminId());
            admin = personService.getPersonById(studyGroupDTO.getGroupAdminId());
        }

        // Преобразование DTO в Entity
        StudyGroup studyGroup = new StudyGroup();
        studyGroup.setCreationDate(LocalDate.now()); // Устанавливаем дату создания
        fromDTO(studyGroup, studyGroupDTO, coordinates, admin, currentUser);

        // Сохранение StudyGroup
        return studyGroupRepository.save(studyGroup);
    }

    public static void fromDTO(StudyGroup studyGroup, StudyGroupDTO dto, Coordinates coordinates, Person admin, User user) {

        studyGroup.setName(dto.getName());
        studyGroup.setCoordinates(coordinates); // Устанавливаем объект Coordinates
        //studyGroup.setCreationDate(LocalDate.now()); // Автоматическая установка даты
        studyGroup.setStudentsCount(dto.getStudentsCount());
        studyGroup.setExpelledStudents(dto.getExpelledStudents());
        studyGroup.setTransferredStudents(dto.getTransferredStudents());

        // Проверка на пустую строку или null для FormOfEducation
        studyGroup.setFormOfEducation(dto.getFormOfEducation() != null && !dto.getFormOfEducation().isEmpty()
                ? FormOfEducation.valueOf(dto.getFormOfEducation())
                : null);

        studyGroup.setShouldBeExpelled(dto.getShouldBeExpelled());

        // Проверка на пустую строку или null для SemesterEnum
        studyGroup.setSemesterEnum(dto.getSemesterEnum() != null && !dto.getSemesterEnum().isEmpty()
                ? Semester.valueOf(dto.getSemesterEnum())
                : null);

        studyGroup.setGroupAdmin(admin); // Устанавливаем объект Admin (если есть)
        studyGroup.setUser(user); // Устанавливаем текущего пользователя
    }

    // Получение группы по ID с проверкой доступа
    public StudyGroup getStudyGroupById(Long id) {
        StudyGroup studyGroup = studyGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StudyGroup not found"));
        return studyGroup;
    }

    public Page<StudyGroup> getAllStudyGroups(Pageable pageable) {
        return studyGroupRepository.findAll(pageable);
    }


    // Обновление группы
    public StudyGroup updateStudyGroup(Long id, StudyGroupDTO studyGroupDTO) {
        StudyGroup existingStudyGroup = getStudyGroupById(id);

        checkAccess(existingStudyGroup);

        // Извлечение Coordinates по id из DTO
        Coordinates coordinates = coordinatesRepository.findById(studyGroupDTO.getCoordinatesId())
                .orElseThrow(() -> new RuntimeException("Coordinates not found"));

        // Извлечение Admin по id из DTO (если указан)
        Person admin = null;
        if (studyGroupDTO.getGroupAdminId() != null) {
            admin = personService.getPersonById(studyGroupDTO.getGroupAdminId());
        }

        // Сохранение текущего состояния в историю
        saveHistory(existingStudyGroup);

        fromDTO(existingStudyGroup, studyGroupDTO, coordinates, admin, existingStudyGroup.getUser());

        return studyGroupRepository.save(existingStudyGroup);
    }

    // Удаление группы
    public void deleteStudyGroup(Long id) {
        StudyGroup studyGroup = getStudyGroupById(id);

        checkAccess(studyGroup);

        // Удаление истории группы
        historyRepository.deleteAll(historyRepository.findByStudyGroupIdOrderByVersionDesc(id));

        studyGroupRepository.deleteById(id);
    }
    // Получение истории изменений группы по id
    public List<StudyGroupHistory> getHistory(Long studyGroupId) {
        return historyRepository.findByStudyGroupIdOrderByVersionDesc(studyGroupId);
    }

    // Найти группу с минимальным expelledStudents
    public StudyGroup getStudyGroupWithMinExpelledStudents() {
        return studyGroupRepository.findWithMinExpelledStudents()
                .orElseThrow(() -> new RuntimeException("No StudyGroups found"));
    }

    // Посчитать группы с adminId больше указанного
    public long countStudyGroupsWithAdminGreaterThan(Long adminId) {
        return studyGroupRepository.countByGroupAdminGreaterThan(adminId);
    }

    // Найти группы с подстрокой в имени
    public List<StudyGroup> getStudyGroupsByNameSubstring(String substring, Pageable pageable) {
        return studyGroupRepository.findByNameContaining(substring, pageable);
    }

    // Отчислить всех студентов
    public void expelAllStudents(Long groupId) {
        StudyGroup studyGroup = getStudyGroupById(groupId);
        checkAccess(studyGroup);

        studyGroup.setExpelledStudents((int) (studyGroup.getExpelledStudents() + studyGroup.getStudentsCount()));
        studyGroup.setStudentsCount(0);

        studyGroupRepository.save(studyGroup);
    }

    // Добавить студента
    public void addStudentToGroup(Long groupId) {
        StudyGroup studyGroup = getStudyGroupById(groupId);
        checkAccess(studyGroup);

        studyGroup.setStudentsCount(studyGroup.getStudentsCount() + 1);
        studyGroupRepository.save(studyGroup);
    }

    private void saveHistory(StudyGroup studyGroup) {
        StudyGroupHistory history = new StudyGroupHistory();
        history.setStudyGroupId(studyGroup.getId());
        history.setName(studyGroup.getName());
        history.setCoordinates(studyGroup.getCoordinates());
        history.setGroupAdmin(studyGroup.getGroupAdmin());
        history.setStudentsCount(studyGroup.getStudentsCount());
        history.setExpelledStudents(studyGroup.getExpelledStudents());
        history.setTransferredStudents(studyGroup.getTransferredStudents());
        history.setFormOfEducation(studyGroup.getFormOfEducation());
        history.setSemesterEnum(studyGroup.getSemesterEnum());
        history.setShouldBeExpelled(studyGroup.getShouldBeExpelled());

        // Определение версии
        int latestVersion = historyRepository.findByStudyGroupIdOrderByVersionDesc(studyGroup.getId())
                .stream()
                .findFirst()
                .map(StudyGroupHistory::getVersion)
                .orElse(0);

        history.setVersion(latestVersion + 1);
        history.setUpdatedAt(LocalDateTime.now());

        // Установка пользователя, который выполняет изменение
        User currentUser = userService.getCurrentUser();
        history.setUpdatedBy(currentUser);

        historyRepository.save(history);
    }


    // Проверка доступа
    private void checkAccess(StudyGroup studyGroup) {
        var currentUser = userService.getCurrentUser();
        if (!studyGroup.getUser().equals(currentUser) && !currentUser.getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("Access denied");
        }
    }

}
