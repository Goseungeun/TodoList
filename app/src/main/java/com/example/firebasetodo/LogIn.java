package com.example.firebasetodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {

    private Button btn_login;
    private EditText edt_ID, edt_PW;
    private TextView tv_signUp;
    private TextView tv_findPW;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){  //이미 로그인 되어있을 경우
            finish();
            startActivity(new Intent(getApplicationContext(),Todolist.class));
        }

        edt_ID = findViewById(R.id.edt_ID);
        edt_PW = findViewById(R.id.edt_PW);
        tv_signUp = findViewById(R.id.tv_signup);
        btn_login = findViewById(R.id.btn_login);
        tv_findPW = findViewById(R.id.tv_findPW);

        tv_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn.this, Register.class);
                startActivity(intent);
                finish();
            }
        });

        //비밀번호 찾기 눌렀을 때
        tv_findPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText str_ID = new EditText(LogIn.this);
                AlertDialog.Builder findPW_dia = new AlertDialog.Builder(LogIn.this);
                findPW_dia.setTitle("비밀번호 찾기");
                findPW_dia.setMessage("아이디를 입력하세요");
                findPW_dia.setView(str_ID);
                findPW_dia.setPositiveButton("비밀번호 찾기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strID = str_ID.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(strID).addOnCompleteListener(LogIn.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    AlertDialog.Builder check = new AlertDialog.Builder(LogIn.this);
                                    check.setTitle("전송완료!");
                                    check.setMessage("이메일을 확인하세요");
                                    check.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Toast.makeText(LogIn.this, "로그인 하기", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    AlertDialog alertDialog = check.create();
                                    alertDialog.show();
                                }
                                else{
                                    Toast.makeText(LogIn.this, "가입되지 않은 이메일입니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                AlertDialog dialog = findPW_dia.create();
                dialog.show();
            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edt_ID.getText().toString().trim();
                String password = edt_PW.getText().toString().trim();
                if(email.isEmpty())
                {
                    Toast.makeText(LogIn.this,"ID가 입력되지 않았습니다. ID를 입력하세요",Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty()){
                    Toast.makeText(LogIn.this,"Password 가 입력되지 않았습니다. Password를 입력하세요",Toast.LENGTH_SHORT).show();
                }

                else{
                    firebaseAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(LogIn.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(LogIn.this,"로그인 성공!",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LogIn.this, Todolist.class);
                                        startActivity(intent);
                                    }

                                    else{
                                        Toast.makeText(LogIn.this,"ID와 PW가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}