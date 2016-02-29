package com.accordance.atlas.model;

import java.util.Objects;

public final class DataCenter {
    private final String userId;
    private final String description;

    private DataCenter(Builder builder) {
        this.userId = builder.userId;
        this.description = builder.description;
    }

    public String getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.userId);
        hash = 29 * hash + Objects.hashCode(this.description);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        final DataCenter other = (DataCenter) obj;
        return Objects.equals(this.userId, other.userId)
                && Objects.equals(this.description, other.description);
    }

    @Override
    public String toString() {
        return "DataCenter{" + "userId=" + userId + ", description=" + description + '}';
    }

    public static final class Builder {
        private final String userId;
        private String description;

        public Builder(String userId) {
            this.userId = Objects.requireNonNull(userId);
            this.description = "";
        }

        public void setDescription(String description) {
            this.description = Objects.requireNonNull(description);
        }

        public DataCenter create() {
            return new DataCenter(this);
        }
    }
}
