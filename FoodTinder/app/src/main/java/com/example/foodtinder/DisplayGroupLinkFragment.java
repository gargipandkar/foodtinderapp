//package com.example.foodtinder;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.fragment.app.Fragment;
//
//public class DisplayGroupLinkFragment extends Fragment {
//
//    public static final String TAG = "DisplayGrpLinkFragment";
//    String grpId;
//    Group currGroup;
//    String currLink;
//
//    private DisplayGroupLinkFragmentListener listener;
//    public interface DisplayGroupLinkFragmentListener {
//        void shareableLink();
//    }
//
////    public CreateEventFragment() {
////        // Required empty public constructor
////    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        View v = inflater.inflate(R.layout.fragment_display_group_link, container, false);
//
//        Toolbar toolbar = v.findViewById(R.id.toolbar);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
////        ((AppCompatActivity)getActivity()).setDisplayHomeAsUpEnabled(true);
////        ((AppCompatActivity)getActivity()).setDisplayShowHomeEnabled(true);
//        ((AppCompatActivity)getActivity()).setTitle("Share Group Link");
//
//
//        currGroup = new Group(grpId);
//        currLink = currGroup.getShareableLink();
//
//        TextView grpLink = v.findViewById(R.id.out_groupLink);
//        Button btn_shareLink = v.findViewById(R.id.btn_shareLink);
//
//        grpLink.setText(currLink);
//        //TODO get and display group link
//
//        btn_shareLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onShareClicked();
//            }
//        });
//
//
//        return v;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        Bundle bundle = this.getArguments();
//
//        if (bundle != null){
//            // get grpId from bundle
//            grpId = bundle.getString("grpId");
//            currGroup = bundle.getParcelable("grpObject");
//            Log.i(TAG, grpId);
//            Log.i(TAG, currGroup.toString());
//        }
//    }
//
//    private void onShareClicked() {
//        Uri link = Uri.parse(currLink); //GET GROUP'S UNIQUE LINK
//
//        Intent sendIntent = new Intent(Intent.ACTION_SEND);
//        sendIntent.setType("text/plain");
//        sendIntent.putExtra(Intent.EXTRA_TEXT, currLink);
//        startActivity(Intent.createChooser(sendIntent, "Share Link"));
//
//    }
//
////    @Override
////    public void onAttach(Context context) {
////        super.onAttach(context);
////        if (context instanceof DisplayGroupLinkFragmentListener){
////            listener = (DisplayGroupLinkFragmentListener) context;
////        } else {
////            throw new RuntimeException(context.toString() + " must implement DisplayGroupLinkFragmentListener");
////        }
////    }
////
////    @Override
////    public void onDetach() {
////        super.onDetach();
////        listener = null;
////    }
//}
