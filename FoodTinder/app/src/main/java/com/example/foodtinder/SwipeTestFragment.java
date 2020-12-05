package com.example.foodtinder;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.example.cardItems.CardStackAdapter;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.HashMap;

public class SwipeTestFragment extends Fragment {

    private static final String TAG = "SwipeTestFragment";
    private FragmentSwipeListener swipeListener;
    private CardStackView cardStackView;
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private int[] number;
    HashMap<String, Integer> listRestVotes;
    HashMap<String, HashMap<String, Object>> listRestInfo;
    HashMap<String, ArrayList<String>> listRestPhotos;
    private ArrayList<String> restAddr, restName;


    public interface FragmentSwipeListener {
    }

    public SwipeTestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null){
            number = bundle.getIntArray("num");
            listRestVotes = (HashMap<String, Integer>) bundle.getSerializable("votes");
            restName = bundle.getStringArrayList("name");
            restAddr = bundle.getStringArrayList("addr");
            Log.i(TAG, "received bundle info");
            Log.i(TAG, restName.toString());
            Log.i(TAG, restAddr.toString());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_swipe_test, container, false);


        cardStackView = v.findViewById(R.id.card_stack_view);
        manager = new CardStackLayoutManager(getActivity(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {

            }

            @Override
            public void onCardSwiped(Direction direction) {
                if (direction == Direction.Right){
                    Context context = getContext();
                    CharSequence text = "Swiped right";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else if (direction == Direction.Left) {
                    Context context = getContext();
                    CharSequence text = "Swiped left";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }

            @Override
            public void onCardRewound() {

            }

            @Override
            public void onCardCanceled() {

            }

            @Override
            public void onCardAppeared(View view, int position) {

            }

            @Override
            public void onCardDisappeared(View view, int position) {

            }
        });

        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new CardStackAdapter(restName, getContext());
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());


        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentSwipeListener){
            swipeListener = (FragmentSwipeListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentSwipeListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        swipeListener = null;
    }
}