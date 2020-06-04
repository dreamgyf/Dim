package com.dreamgyf.dim;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dreamgyf.dim.adapter.SearchFriendListViewAdapter;
import com.dreamgyf.dim.data.StaticData;
import com.dreamgyf.dim.entity.Group;
import com.dreamgyf.dim.entity.httpresp.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchFriendOrGroupActivity extends AppCompatActivity {

    private TextView addFriendButton;

    private TextView addGroupButton;

    private View addFriendView;

    private View addGroupView;

    private SearchView searchFriend;

    private SearchView searchGroup;

    private ListView friendListView;

    private ListView groupListView;

    private List<User> friendList = new ArrayList<>();

    private List<Group> groupList = new ArrayList<>();

    private SearchFriendListViewAdapter searchFriendListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend_or_group);

        initFriendView();
        initGroupView();
        initTop();
    }

    public void initFriendView() {
        addFriendView = findViewById(R.id.addFriendView);
        searchFriend = findViewById(R.id.searchFriend);
        searchFriend.setIconifiedByDefault(false);
        friendListView = findViewById(R.id.friendListView);
        friendListView.setAdapter(searchFriendListViewAdapter = new SearchFriendListViewAdapter(this,friendList));
        searchFriend.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFriend.clearFocus();
                friendList.clear();
                searchFriendListViewAdapter.notifyDataSetChanged();
                AsyncTask<String,Void,List<User>> task = new AsyncTask<String, Void, List<User>>() {
                    @Override
                    protected void onPostExecute(List<User> users) {
                        if(users != null) {
                            friendList.addAll(users);
                            searchFriendListViewAdapter.notifyDataSetChanged();
                        }
                        else {
                            Toast toast = Toast.makeText(SearchFriendOrGroupActivity.this,"",Toast.LENGTH_SHORT);
                            toast.setText("获取数据失败");
                            toast.show();
                        }
                    }

                    @Override
                    protected List<User> doInBackground(String... strings) {
                        List<User> res = null;
                        try {
                            URL url = new URL(StaticData.DOMAIN + "/friend/search?myId=" + StaticData.my.getId() + "&keyword=" + strings[0]);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setRequestMethod("POST");
                            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                                String resp = "";
                                String line;
                                while((line = in.readLine()) != null)
                                    resp += line;
                                in.close();
                                final JsonObject jsonObject = new JsonParser().parse(resp).getAsJsonObject();
                                if("0".equals(jsonObject.get("code").getAsString())) {
                                    Gson gson = new Gson();
                                    JsonObject data = jsonObject.get("data").getAsJsonObject();
                                    res = gson.fromJson(data.get("friendList"), new TypeToken<List<User>>(){}.getType());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                        return res;
                    }
                };
                task.execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = friendList.get(position);
                Intent intent = new Intent(SearchFriendOrGroupActivity.this,UserInfoActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
    }

    public void initGroupView() {
        addGroupView = findViewById(R.id.addGroupView);
        searchGroup = findViewById(R.id.searchGroup);
        searchGroup.setIconifiedByDefault(false);
        groupListView = findViewById(R.id.groupListView);
    }

    public void initTop() {
        addFriendButton = findViewById(R.id.addFriendButton);
        addGroupButton = findViewById(R.id.addGroupButton);
        selectAddFriend();
        addFriendButton.setOnClickListener((view)->{
            selectAddFriend();
        });
        addGroupButton.setOnClickListener((view)->{
            selectAddGroup();
        });
    }

    public void selectAddFriend() {
        addFriendButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        addFriendButton.setBackgroundResource(R.drawable.shape_add_friend_button);
        addGroupButton.setTextColor(Color.WHITE);
        addGroupButton.setBackgroundColor(Color.TRANSPARENT);
        addFriendView.setVisibility(View.VISIBLE);
        addGroupView.setVisibility(View.INVISIBLE);
    }

    public void selectAddGroup() {
        addGroupButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        addGroupButton.setBackgroundResource(R.drawable.shape_add_group_button);
        addFriendButton.setTextColor(Color.WHITE);
        addFriendButton.setBackgroundColor(Color.TRANSPARENT);
        addGroupView.setVisibility(View.VISIBLE);
        addFriendView.setVisibility(View.INVISIBLE);
    }
}
