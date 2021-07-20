package com.example.firebasetodo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
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
import java.util.List;
import java.util.Map;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder>{
    public Object setItem;
    private ArrayList<Todo> items;
    private Context mContext;
    private DatabaseReference mdatabase;
    private FirebaseAuth firebaseAuth;
    private Todolist todolist;      //Todolist화면의 context를 받아오기 위함

    TodoAdapter(ArrayList<Todo> items, Context mContext){
        this.items = items;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.todo_item,viewGroup,false);
        todolist = (Todolist)Todolist.context;

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Todo item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Todo item){
        items.add(item);
        notifyItemChanged(0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CheckBox todo_contents;
        private ImageView btn_delete;
        private ImageView btn_modify;

        public ViewHolder(View itemView){
            super(itemView);

            todo_contents = itemView.findViewById(R.id.todo_item);
            btn_delete = itemView.findViewById(R.id.btn_delete);
            btn_modify = itemView.findViewById(R.id.btn_modify);
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String uid = user.getUid();

            btn_modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int curPos = getAdapterPosition();
                    Todo todoItem = items.get(curPos);

                    final EditText new_contents = new EditText(mContext);

                    AlertDialog.Builder modify_dia = new AlertDialog.Builder(mContext);
                    modify_dia.setTitle("일정 수정");
                    modify_dia.setView(new_contents);
                    modify_dia.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //UI 변경
                            String contents = new_contents.getText().toString();
                            todoItem.setTodo_contents(contents);
                            notifyItemChanged(curPos,todoItem);
                            //데이터베이스 수정
                            todolist.onModifyContent(uid,contents,curPos);
                        }
                    });

                    modify_dia.show();
                }
            });

            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int curPos = getAdapterPosition();
                    Todo todoItem = items.get(curPos);
                    //UI변경
                    items.remove(curPos);
                    notifyItemRemoved(curPos);
                    //데이터베이스 삭제
                    todolist.onDeleteContent(uid,curPos);
                }
            });
        }
        public void setItem(Todo item){
            todo_contents.setText(item.getTodo_contents());
        }
    }
}
