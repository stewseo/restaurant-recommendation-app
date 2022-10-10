package com.example.client.yelp_fusion.model;

import java.util.*;

public interface TestWarningsHandler {

    boolean warningsShouldFailRequest(List<String> warnings);

    TestWarningsHandler PERMISSIVE = new TestWarningsHandler() {
        @Override
        public boolean warningsShouldFailRequest(List<String> warnings) {
            return false;
        }

        @Override
        public String toString() {
            return "permissive";
        }
    };
    TestWarningsHandler STRICT = new TestWarningsHandler() {
        @Override
        public boolean warningsShouldFailRequest(List<String> warnings) {
            return false == warnings.isEmpty();
        }

        @Override
        public String toString() {
            return "strict";
        }
    };
}

