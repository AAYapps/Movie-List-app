package e.thejo.movieslist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class MovielistActivity extends AppCompatActivity {


    public void gotoAddMovie(View view) {
        Intent i = new Intent(this, AddMovieActivity.class);
        startActivityForResult(i, 1);
    }

    public class listItems
    {
        private final ArrayList<String> Title = new ArrayList<String>();
        private final ArrayList<String> imdb = new ArrayList<String>();

        public void addListItems(String title, String link)
        {
            Title.add(title);
            imdb.add(link);
        }

        public void removeMovie(int index)
        {
            Title.remove(index);
            imdb.remove(index);
        }

        public ArrayList<String> toArrayString()
        {
            return Title;
        }

        public ArrayList<String> toLink()
        {
            return imdb;
        }
    }

    private final listItems movies = new listItems();

    ListView movieList;
    ArrayAdapter<String> adapter;
    int deleteIndex = 0;

    private void AddMovie(String Title, String imdb)
    {
        movies.addListItems(Title, imdb);
    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data)
    {
        AddMovie(data.getStringExtra("movieTitle"), data.getStringExtra("movieIMDB"));
        movieList.setAdapter(adapter);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        SharedPreferences.Editor e = p.edit();
        e.clear();
        for (int i = 0; i < movies.toArrayString().size(); i++)
        {
            e.putString(movies.toArrayString().get(i), movies.toLink().get(i));
        }
        e.apply();
    }
    SharedPreferences p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        p = getPreferences(Context.MODE_PRIVATE);
        Map<String, String> map = (Map<String, String>) p.getAll();

        movieList = findViewById(R.id.mList);
        if (!map.isEmpty())
        {
            Collection<String> s = map.keySet();
            String[] str = s.toArray(new String[0]);
            for (int i = 0; i < map.size(); i++)
            {
                movies.addListItems(str[i], map.get(str[i]));

            }
        }
        else
        {
            AddMovie("Jaws", "0073195");
            AddMovie("Back to the Future", "0088763");
            AddMovie("Ghostbusters", "0087332");
        }

        adapter = new ArrayAdapter<String>(this, R.layout.list_item_view, movies.toArrayString());
        movieList.setAdapter(adapter);

        movieList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String url = "https://www.imdb.com/title/tt" + movies.toLink().get(i) + "/";
                Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(in);;

            }
        });

        movieList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                deleteIndex = i;
                AlertDialog checkRemoveChoice = new AlertDialog.Builder(MovielistActivity.this).create();
                checkRemoveChoice.setMessage("Are you sure you want to delete this movie?");
                checkRemoveChoice.setTitle("Delete Movie");
                checkRemoveChoice.setButton(AlertDialog.BUTTON_POSITIVE, "Delete " + movies.toArrayString().get(i), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        movies.removeMovie(deleteIndex);
                        movieList.setAdapter(adapter);
                    }
                });
                checkRemoveChoice.setButton(AlertDialog.BUTTON_NEGATIVE, "Never Mind", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        dialogInterface.dismiss();
                    }
                });
                checkRemoveChoice.show();

                return true;
            }
        });
    }
}
