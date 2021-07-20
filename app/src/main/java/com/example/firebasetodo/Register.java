package com.example.firebasetodo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    EditText edt_RegisterID, edt_RegisterPW, edt_CheckPW, edt_registerName, edt_registerPhone;
    Button btn_register,btn_cancel;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        firebaseAuth = FirebaseAuth.getInstance();      //회원 관리를 위한 객체
        mDatabase = FirebaseDatabase.getInstance().getReference();      //데이터베이스 관리

        edt_RegisterID = findViewById(R.id.edt_RegisterID);
        edt_RegisterPW = findViewById(R.id.edt_RegisterPW);
        edt_CheckPW = findViewById(R.id.edt_CheckPW);
        edt_registerName = findViewById(R.id.edt_registerName);
        edt_registerPhone = findViewById(R.id.edt_registerPhone);
        btn_register = findViewById(R.id.btn_register);
        btn_cancel = findViewById(R.id.btn_cancel);

        //REGISTER 버튼 클릭시
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edt_RegisterID.getText().toString().trim();
                String password = edt_RegisterPW.getText().toString().trim();
                String pwcheck = edt_CheckPW.getText().toString().trim();
                String name = edt_registerName.getText().toString().trim();
                String phone = edt_registerPhone.getText().toString().trim();

                if (email.isEmpty()||password.isEmpty()||pwcheck.isEmpty()||name.isEmpty()||phone.isEmpty())
                {   //입력이 안된 정보가 있을 때, 모든 정보를 입력하라고 알림을 준다.
                    Toast.makeText(Register.this, "정보가 입력되지 않았습니다.\n 모든 정보를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(password.equals(pwcheck)){
                        // 비밀번호와 비밀번호 확인이 같을 때, 회원 정보를 데이터베이스에 저장하고 가입시킨다.
                        firebaseAuth.createUserWithEmailAndPassword(email,password)     //회원 생성
                                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            // 가입이 잘 되었을 때
                                            //DB에 저장
                                            final String uid = task.getResult().getUser().getUid();
                                            Member member = new Member();
                                            member.name = name;
                                            member.email = email;
                                            member.phone_num = phone;
                                            mDatabase.child("users").child(uid).setValue(member);
                                            firebaseAuth.signOut();     //가입된 회원 로그아웃
                                            //로그인 화면으로 이동
                                            Intent intent = new Intent(Register.this,LogIn.class);
                                            startActivity(intent);
                                            finish();
                                            //회원가입 성공을 알리는 토스트 메세지
                                            Toast.makeText(Register.this, " 회원 가입에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(Register.this,"가입실패. \n 다시 시도하세요.",Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                });
                    }
                    else {
                        //비밀번호와 비밀번호 확인이 일치하지 않을 때
                        Toast.makeText(Register.this, "비밀번호가 일치하지 않습니다. \n다시 입력해 주세요",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        //CANCEL 버튼 클릭 시
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                builder.setTitle("회원가입 취소").setMessage("정말 취소하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Register.this,LogIn.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Register.this, "회원가입 화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }
}
