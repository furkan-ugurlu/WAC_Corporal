// ReflectionRequest.java
package com.Savarona.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ReflectionRequest {

    @NotBlank(message = "Content must not be blank")
    @Size(max = 5000, message = "Content maximum length is 5000 characters")
    private String content;

    // Constructors
    public ReflectionRequest() {}

    public ReflectionRequest(String content) {
        this.content = content;
    }

    // Getter & Setter
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}