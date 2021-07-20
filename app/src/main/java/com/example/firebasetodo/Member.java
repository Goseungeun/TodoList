package com.example.firebasetodo;

import java.util.ArrayList;

public class Member {
    String email;
    String name;
    String phone_num;
    ArrayList<Todo> todolist;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public ArrayList<Todo> getTodolist() {
        return todolist;
    }

    public void setTodolist(ArrayList<Todo> todolist) {
        this.todolist = todolist;
    }
}
