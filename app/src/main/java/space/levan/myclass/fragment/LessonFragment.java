package space.levan.myclass.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import space.levan.myclass.R;

/**
 * Created by 339 on 2016/5/5.
 */
public class LessonFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        TextView text = new TextView(container.getContext());
        text.setText("(╯°Д°)╯︵┴┴");
        text.setGravity(Gravity.CENTER);


        //mListView

        return text;
    }

    public View setListView(ViewGroup container,Context context, List<HashMap<String, Object>> info,
                            TextView mName,TextView mTeacher,TextView mTime,TextView mRoom,
                            String name, String teacher, String time, String room) {

        ListView mListView = new ListView(container.getContext());

        SimpleAdapter adapter = new SimpleAdapter(context,info, R.layout.item_class,
                new String[]{"Name","Teacher","Time","Room"},
                new int[]{R.id.class_name,R.id.class_teacher,R.id.class_time,R.id.class_room});
        mName.setText(name);
        mTeacher.setText(teacher);
        mTime.setText(time);
        mRoom.setText(room);
        mListView.setAdapter(adapter);

        return mListView;
    }
}
