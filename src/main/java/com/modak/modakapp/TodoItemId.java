package com.modak.modakapp;

import com.modak.modakapp.domain.Todo;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

public class TodoItemId implements Serializable {
    int id;
    Todo todo;
}
