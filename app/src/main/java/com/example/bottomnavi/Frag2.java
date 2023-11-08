package com.example.bottomnavi;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Frag2 extends Fragment {
    private static final int PICK_IMAGE = 1;
    private Uri selectedImageUri;
    private DatabaseReference databaseReference; //파이어베이스


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate (R.layout.frag2, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference("posts"); // 파이어베이스에 POSTS라는 키로 값을 보냄

        Button chooseImageButton = view.findViewById(R.id.chooseImageButton2); //이미지 선택 버튼
        Button postButton = view.findViewById(R.id.postButton2); //게시 버튼
        EditText editText = view.findViewById(R.id.editText2); //글작성 칸
        ImageView imageView = view.findViewById(R.id.imageView2); //이미지 미리보기

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI); //갤러리 인텐트
                startActivityForResult(gallery, PICK_IMAGE); //갤러리에서 선택한 이미지 전송
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userText = editText.getText().toString(); //게시글 작성한 부분을 userText에 저장
                if (selectedImageUri != null) {
                    String postId = databaseReference.push().getKey(); //postId에 파이어베이스 키 저장
                    //PostData클래스 post생성자에 매개변수로 보낸 후 post에 저장.(파이어베이스로 보낼 데이터)
                    com.example.bottomnavi.PostData post = new com.example.bottomnavi.PostData(userText, selectedImageUri.toString());
                    if (postId != null) {
                        databaseReference.child(postId).setValue(post); //키값과 데이터값을 파이어베이스에 전송
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == PICK_IMAGE) {
            selectedImageUri = data.getData();
            ImageView imageView = getView().findViewById(R.id.imageView2);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(selectedImageUri);
        }
    }
}
