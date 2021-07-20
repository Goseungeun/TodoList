package com.example.firebasetodo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Todolist extends AppCompatActivity {

    private RecyclerView rv_todo;
    private TodoAdapter mAdapter;
    private ArrayList<Todo> todoItems;
    private ArrayList<String> uidList = new ArrayList<String>();
    private TextView btn_save;
    private EditText input_todo;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private ImageView btn_myPage;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);
        todoItems = new ArrayList<Todo>();      //Todo 배열 생성

        btn_save = findViewById(R.id.btn_save);
        rv_todo = findViewById(R.id.todorecycle);
        input_todo = findViewById(R.id.edt_InputTodo);
        btn_myPage = findViewById(R.id.iv_myPage);

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new TodoAdapter(todoItems, this);

        rv_todo.setLayoutManager(mLayoutManager);
        rv_todo.setAdapter(mAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        context = this;

        if (user == null) {
            //로그인 된 정보가 없을 때
            finish();
            startActivity(new Intent(Todolist.this, LogIn.class));
        }

        final String uid = user.getUid();
        DatabaseReference userTodoList = mDatabase.child("users").child(uid).child("todolist");

        //데이터 베이스에 변화 생겼을 때
        userTodoList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todoItems.clear();
                uidList.clear();
                for (DataSnapshot todoSnapshot : snapshot.getChildren()) {
                    String contents = todoSnapshot.child("todo_contents").getValue(String.class);
                    String uidKey = todoSnapshot.getKey();
                    Todo item = new Todo();
                    item.setTodo_contents(contents);
                    todoItems.add(item);
                    uidList.add(uidKey);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Failed to read value");
                Toast.makeText(Todolist.this, "목록을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        //저장버튼 클릭시 이벤트
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String add_todo = input_todo.getText().toString();

                if (add_todo.isEmpty()) {
                    //입력한 값이 없을 때
                    Toast.makeText(Todolist.this, "할 일을 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    input_todo.setText("");     //edittext의 text를 다시 없애준다.
                    Todo todo = new Todo();
                    todo.setTodo_contents(add_todo);
                    userTodoList.push().setValue(todo);     //데이터베이스에 목록 아이템들 추가

                }
            }
        });

        btn_myPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(Todolist.this, MyPage.class);
                startActivity(intent);
            }
        });

    }

    public void onModifyContent(String uid,String new_content,int position){
        Map<String,Object> updateMap = new HashMap<>();
        updateMap.put("todo_contents",new_content);
        mDatabase.child("users").child(uid).child("todolist").child(uidList.get(position)).updateChildren(updateMap);
    }

    public void onDeleteContent(String uid, int position){
        mDatabase.child("users").child(uid).child("todolist").child(uidList.get(position)).removeValue();
    }
}

