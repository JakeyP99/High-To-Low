//package com.example.countingdowngame;
//
//import android.content.Context;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.NumberPicker;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.Arrays;
//
//
//public class WildCardChoice extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.wildcard_edit);
//    }
//
//
//        public void showAddWildCardDialog (View view){
//            // Create an AlertDialog with an EditText for the wild card text and a NumberPicker for the probability
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Add Wild Card");
//
//            final EditText wildCardEditText = new EditText(this);
//            wildCardEditText.setHint("Enter Wild Card Text");
//            builder.setView(wildCardEditText);
//
//            final NumberPicker probabilityPicker = new NumberPicker(this);
//            probabilityPicker.setMinValue(1);
//            probabilityPicker.setMaxValue(10);
//            builder.setView(probabilityPicker);
//
//            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    String wildCardText = wildCardEditText.getText().toString();
//                    int probability = probabilityPicker.getValue();
//
//                    // Add the new wild card to the array
//                    WildCardProbabilities newWildCard = new WildCardProbabilities(wildCardText, probability);
//                    WildCardProbabilities[] activityProbabilities = Arrays.copyOf(activityProbabilities, activityProbabilities.length + 1);
//                    activityProbabilities[activityProbabilities.length - 1] = newWildCard;
//
//                    // Update the ListView to show the updated list of wild cards
//                    ListView listView = findViewById(R.id.listView_WildCard);
//                    WildCardAdapter adapter = new WildCardAdapter(WildCardChoice.this, activityProbabilities);
//                    listView.setAdapter(adapter);
//                }
//            });
//
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//
//            builder.show();
//        }
//    }
//
//    public class WildCardAdapter extends ArrayAdapter<WildCardProbabilities> {
//
//        private Context context;
//        private WildCardProbabilities[] wildCards;
//
//        public WildCardAdapter(Context context, WildCardProbabilities[] wildCards) {
//            super(context, R.layout.wildcard_item, wildCards);
//            this.context = context;
//            this.wildCards = wildCards;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View rowView = inflater.inflate(R.layout.wildcard_item, parent, false);
//
//            TextView wildCardTextView = rowView.findViewById(R.id.textView_WildCardItem);
//            wildCardTextView.setText(wildCards[position].getWildCardText());
//
//            TextView probabilityTextView = rowView.findViewById(R.id.textView_ProbabilityItem);
//
//
//            WildCardProbabilities[] activityProbabilities = {
//                    new WildCardProbabilities("Take 1 drink.", 10),
//                    new WildCardProbabilities("Take 2 drinks.", 8),
//            };
//
//            probabilityTextView.setText(String.format("%d%%", wildCards[position].getProbability() * 10));
//
//            return rowView;
//        }
//    }
//
