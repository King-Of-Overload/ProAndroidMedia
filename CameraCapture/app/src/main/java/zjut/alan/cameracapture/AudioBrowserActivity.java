package zjut.alan.cameracapture;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import java.io.File;

/**
 * 唱片集浏览器
 */
public class AudioBrowserActivity extends ListActivity {
    private Cursor cursor;
    public static int STATE_SELECT_ALBUM = 0;
    public static int STATE_SELECT_SONG = 1;
    private int currentState = STATE_SELECT_ALBUM;
    private String[] columns = {
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.MIME_TYPE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_browser);
        String[] columns = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM
        };
        cursor = managedQuery(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                columns, null, null, null);
        String[] displayFields = new String[]{MediaStore.Audio.Albums.ALBUM};
        int[] displayViews = new int[]{android.R.id.text1};
        setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                cursor, displayFields, displayViews));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(currentState == STATE_SELECT_ALBUM){
            if(cursor.moveToPosition(position)){
                String where = MediaStore.Audio.Media.ALBUM + "=?";
                String whereVal[] = {
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM))
                };
                String orderBy = MediaStore.Audio.Media.TITLE;
                cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        columns, where, whereVal, orderBy);
                String[] displayFields = new String[]{MediaStore.Audio.Media.DISPLAY_NAME};
                int[] displayViews = new int[]{android.R.id.text1};
                setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                        cursor, displayFields, displayViews));
                currentState = STATE_SELECT_SONG;
            }
        }else if(currentState == STATE_SELECT_SONG){
            if(cursor.moveToPosition(position)){
                int fileColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int mimeTypeColumn = cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);
                String audioFilePath = cursor.getString(fileColumn);
                String mimeType = cursor.getString(mimeTypeColumn);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                File newFile = new File(audioFilePath);
                intent.setDataAndType(Uri.fromFile(newFile), mimeType);
                startActivity(intent);
            }
        }
    }
}
