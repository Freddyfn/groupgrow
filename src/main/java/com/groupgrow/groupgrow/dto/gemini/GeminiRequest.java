package com.groupgrow.groupgrow.dto.gemini;

import java.util.Collections;
import java.util.List;

public class GeminiRequest {

    private final List<Content> contents;

    public GeminiRequest(String text) {
        Part part = new Part(text);
        Content content = new Content(Collections.singletonList(part));
        this.contents = Collections.singletonList(content);
    }

    public List<Content> getContents() {
        return contents;
    }

    // Clases anidadas para representar la estructura JSON
    public static class Content {
        private final List<Part> parts;

        public Content(List<Part> parts) {
            this.parts = parts;
        }

        public List<Part> getParts() {
            return parts;
        }
    }

    public static class Part {
        private final String text;

        public Part(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
