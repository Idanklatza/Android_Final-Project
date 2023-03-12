package com.example.travelapp;

import static com.example.travelapp.MyApplication.getMyContext;
import static com.example.travelapp.model.Model.isOnline;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.travelapp.databinding.FragmentPostPageBinding;
import com.example.travelapp.model.Model;
import com.squareup.picasso.Picasso;


public class PostFragment extends Fragment {
    String title;
    String description;
    String avatarImg;
    FragmentPostPageBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  // save state
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentPostPageBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        // save element argument
        getElement();

        // set the details post into ui
        if (title != null){
            binding.postTitleTv.setText(title);
        }
        if (description != null){
            binding.DescriptionTv.setText(description);
        }
        if (avatarImg.isEmpty() || avatarImg == ""){
            binding.avatarImg.setImageResource(R.drawable.nophoto);

        }else{
            Picasso.get().load(avatarImg).error(R.drawable.error).into(binding.avatarImg);
        }

        // get the status of star post and set him to ui
        if(isOnline(getMyContext())) {
            Model.instance().getAllStars(stars -> {
                if (stars.contains(title))
                    binding.star.setChecked(true);
                else {
                    binding.star.setChecked(false);
                }
            });
        }

        binding.backBtn.setOnClickListener((view1)->{
            Navigation.findNavController(view1).popBackStack();
        });

        // when click star or click off star > update the stars in firebase
        binding.star.setOnClickListener(view1->{
            Model.instance().saveStar(title);
        });

        return view;
    }

    public void getElement() {
        title = PostFragmentArgs.fromBundle(getArguments()).getNamePost();
        description = PostFragmentArgs.fromBundle(getArguments()).getDescription();
        avatarImg = (PostFragmentArgs.fromBundle(getArguments()).getAvatarUrl());
    }
}