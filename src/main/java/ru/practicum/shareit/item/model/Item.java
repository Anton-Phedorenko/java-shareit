package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * TODO Sprint add-controllers.
 */
@Component
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;

    public Item() {
    }

    public Item(String name, String description, Boolean available, Long ownerId, Long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.ownerId = ownerId;
        this.requestId = requestId;
    }

    public Item(Long id, String name, String description, Boolean available, Long ownerId, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.ownerId = ownerId;
        this.requestId = requestId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) && Objects.equals(name, item.name) && Objects.equals(description, item.description) && Objects.equals(available, item.available) && Objects.equals(ownerId, item.ownerId) && Objects.equals(requestId, item.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, ownerId, requestId);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                ", ownerId=" + ownerId +
                ", requestId=" + requestId +
                '}';
    }
}
