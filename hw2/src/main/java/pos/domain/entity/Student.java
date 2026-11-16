package pos.domain.entity;

public class Student {
    private final Long id;
    private String fullName;
    private String group;

    public Student(Long id, String fullName, String group) {
        if (id == null) throw new IllegalArgumentException("ID не может быть пустым.");
        if (fullName == null || fullName.isBlank()) throw new IllegalArgumentException("ФИО не может быть пустым.");
        this.id = id;
        this.fullName = fullName.trim();
        this.group = validGroup(group);
    }

    public void rename(String newFullName) {
        if (newFullName == null || newFullName.isBlank())
            throw new IllegalArgumentException("ФИО не может быть пустым.");
        this.fullName = newFullName.trim();
    }

    public void changeGroup(String newGroup) {
        this.group = validGroup(newGroup);
    }

    private String validGroup(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Группа не может быть пустой.");
        return value.trim();
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGroup() {
        return group;
    }
}
