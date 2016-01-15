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
