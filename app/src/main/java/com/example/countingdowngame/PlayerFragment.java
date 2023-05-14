//package com.example.countingdowngame;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.fragment.app.Fragment;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//public class PlayerFragment extends Fragment {
//    private Game gameInstance;
//    private TextView numberText;
//    private TextView nextPlayerText;
//    private Button btnSkip;
//    private Button btnWild;
//
//    private View wildText;
//    private Button btnGenerate;
//    private Button btnBackWild;
//    private Set<WildCardProbabilities> usedWildCards = new HashSet<>();
//
//    private Map<Player, Set<WildCardProbabilities>> usedWildCard = new HashMap<>();
//
//    public PlayerFragment(Game gameInstance) {
//        this.gameInstance = gameInstance;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.player_fragment, container, false);
//
//        numberText = view.findViewById(R.id.numberText);
//        nextPlayerText = view.findViewById(R.id.textView_Number_Turn);
//        btnSkip = view.findViewById(R.id.btnSkip);
//        btnWild = view.findViewById(R.id.btnWild);
//        wildText = view.findViewById(R.id.wild_textview);
//        btnGenerate = view.findViewById(R.id.btnGenerate);
//        btnBackWild = view.findViewById(R.id.btnBackWildCard);
//
//        // Set up button listeners and logic here...
//
//        return view;
//    }
//
//    // Add button listener methods and other logic here...
//}
