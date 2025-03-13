package com.soumyajit.healthhub.Exception;

public class ResourceNotFound extends RuntimeException {
    public ResourceNotFound(String message) {
      super(message);
    }
}
