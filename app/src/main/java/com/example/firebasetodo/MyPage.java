package com.example.firebasetodo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

public class MyPage extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mdatabase;

    private TextView memberName;
    private TextView memberPhone;
    private TextView btn_ChangName;
    private TextView btn_ChangPhone;
    private Button btn_logout;
    private Button btn_withdraw;
    private ImageView iv_MyPageBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);

        firebaseAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference();

        memberName = findViewById(R.id.memberName);
        memberPhone = findViewById(R.id.memberPhone);
        btn_ChangName = findViewById(R.id.btn_ChangeName);
        btn_ChangPhone = findViewById(R.id.btn_ChangePhone);
        btn_logout = findViewById(R.id.btn_logout);
        btn_withdraw = findViewById(R.id.btn_withdraw);
        iv_MyPageBack = findViewById(R.id.iv_myPageBack);

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LogIn.class));
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();      //현제 로그인 된 회원의 정보를 받아온다.
        final String uid = user.getUid();
        DatabaseReference userInfo = mdatabase.child("users").child(uid);       //데이터 베이스 연결하기 위해

        userInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String phone = snapshot.child("phone_num").getValue(String.class);
                memberName.setText(name);
                memberPhone.setText(phone);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그아웃 버튼 눌렀을 때
                firebaseAuth.signOut();
                Toast.makeText(MyPage.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MyPage.this, LogIn.class);
                startActivity(intent);
            }
        });

        btn_ChangName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Name 옆에 change 눌렀을 때
                final EditText new_name = new EditText(MyPage.this);
                AlertDialog.Builder modify_dia = new AlertDialog.Builder(MyPage.this);
                modify_dia.setTitle("이름 수정");
                modify_dia.setView(new_name);
                modify_dia.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String n_name = new_name.getText().toString().trim();           //공백 없이 받아오기
                        Map<String ,Object> updateMap = new HashMap<>();
                        updateMap.put("name",n_name);
                        //DB 변경
                        userInfo.updateChildren(updateMap);
                    }
                });
                modify_dia.show();
            }
        });

        btn_ChangPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Phone 옆에 change 눌렀을 때
                final EditText new_phone = new EditText(MyPage.this);
                AlertDialog.Builder modify_dia = new AlertDialog.Builder(MyPage.this);
                modify_dia.setTitle("휴대폰 번호 수정");
                modify_dia.setView(new_phone);
                modify_dia.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String n_phone_num = new_phone.getText().toString().trim();           //공백 없이 받아오기
                        Map<String ,Object> updateMap = new HashMap<>();
                        updateMap.put("phone_num",n_phone_num);
                        //DB 변경
                        userInfo.updateChildren(updateMap);
                    }
                });
                modify_dia.show();
            }
        });

        btn_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //회원탈퇴 버튼 클릭시
                firebaseAuth.signOut();
                user.delete().addOnCompleteListener(MyPage.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MyPage.this,"회원탈퇴되었습니다.",Toast.LENGTH_SHORT).show();
                            userInfo.removeValue();     //DB에서 삭제
                        }
                    }
                });
                Intent intent = new Intent(MyPage.this, LogIn.class);
                startActivity(intent);
                finish();
            }
        });

        iv_MyPageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPage.this,Todolist.class);
                startActivity(intent);
                finish();
            }
        });
    }

}