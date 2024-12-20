# Лабораторная работа №2

**Вариант ??????**

**Доработать ИС из ЛР1 следующим образом:**

- Добавить в систему возможность массового добавления объектов при помощи импорта файла. Формат для импорта необходимо согласовать с преподавателем. Импортируемый файл должен загружаться на сервер через интерфейс разработанного веб-приложения.
    - При реализации логики импорта объектов необходимо реализовать транзакцию таким образом, чтобы в случае возникновения ошибок при импорте, не был создан ни один объект.
    - При импорте должна быть реализована проверка пользовательского ввода в соответствии с ограничениями предметной области из ЛР1.
    - При наличии вложенных объектов в основной объект из ЛР1 необходимо задавать значения полей вложенных объектов в той же записи, что и основной объект.

- Необходимо добавить в систему интерфейс для отображения истории импорта (обычный пользователь видит только операции импорта, запущенные им, администратор - все операции).
    - В истории должны отображаться id операции, статус ее завершения, пользователь, который ее запустил, число добавленных объектов в операции (только для успешно завершенных).

- Согласовать с преподавателем и добавить в модель из первой лабораторной новые ограничения уникальности, проверяемые на программном уровне (эти новые ограничения должны быть реализованы в рамках бизнес-логики приложения и не должны быть отображены/реализованы в БД).

- Реализовать сценарий с использованием Apache JMeter, имитирующий одновременную работу нескольких пользователей с ИС, и проверить корректность изоляции транзакций, используемых в ЛР. По итогам исследования поведения системы при ее одновременном использовании несколькими пользователями изменить уровень изоляции транзакций там, где это требуется. Обосновать изменения.
    - Реализованный сценарий должен покрывать создание, редактирование, удаление и импорт объектов.
    - Реализованный сценарий должен проверять корректность поведения системы при попытке нескольких пользователей обновить и\или удалить один и тот же объект (например, двух администраторов).
    - Реализованный сценарий должен проверять корректность соблюдения системой ограничений уникальности предметной области при одновременной попытке нескольких пользователей создать объект с одним и тем же уникальным значением.

**Содержание отчёта:**

1. Текст задания.
2. UML-диаграммы классов и пакетов разработанного приложения.
3. Исходный код системы или ссылка на репозиторий с исходным кодом.
4. Выводы по работе.

**Вопросы к защите лабораторной работы:**

1. Понятие бизнес-логики в программных системах. Уровень бизнес-логики в многоуровневой архитектуре программных систем.
2. Jakarta Enterprise Beans (EJB). Виды бинов и их назначение
3. EJB Session beans. Жизненный цикл.
4. Понятие транзакции. Транзации в БД. ACID
5. Виды конфликтов при многопользовательской работе с данными. Уровни изоляции транзакций.
6. Особенности реализации транзакций на уровне бизнес-логики, отличия от транзакций на уровне БД.
7. Java Transaction API. Основные принципы и программные интерфейсы.
8. Реализация управления транзакциями в Jakarta EE. Декларативное и программное управление транзакциями.
9. Реализация управления транзакциями в Spring. Декларативное и программное управление транзакциями в Spring. Аннотация @Transactional.
