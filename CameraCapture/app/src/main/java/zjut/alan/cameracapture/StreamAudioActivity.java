package zjut.alan.cameracapture;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import zjut.alan.cameracapture.model.PlaylistFile;

/**
 *HTTP流式音頻
 * 通用：PLS（MIME：audio/x-scpls）  M3U(audio/x-mpegurl)
 * e.g.  M3U
 * #EXTM3U     是必須的，指定下面是一個擴展的M3U文件
 * #EXTINF:0,Live Stream Name   額外信息   持續時間，媒體名稱，該行可以重複
 * http://.....:8080/
 * Android上無法直接分析M3U文件，需自行處理
 */
public class StreamAudioActivity extends AppCompatActivity
        implements View.OnClickListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener {
    private Vector playListItems;//向量保存條目
    private Button parseButton;
    private Button playButton, stopButton;
    private EditText editTextUrl;
    private String baseURL = "";
    private MediaPlayer mediaPlayer;
    private int currentPlaylistItemNumber = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_audio);
        parseButton = findViewById(R.id.ButtonParse);
        playButton = findViewById(R.id.PlayButton);
        stopButton = findViewById(R.id.StopButton);
        editTextUrl = findViewById(R.id.EditTextURL);
        editTextUrl.setText("http://pubint.ic.llnwd.net/stream/pubint_kmfa.m3u");
        parseButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        playButton.setEnabled(false);
        stopButton.setEnabled(false);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.stop();
        mediaPlayer.reset();
        if(playListItems.size() > currentPlaylistItemNumber + 1){
            currentPlaylistItemNumber ++;
            String path = ((PlaylistFile)playListItems.get(currentPlaylistItemNumber)).getFilePath();
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        stopButton.setEnabled(true);
        mediaPlayer.start();
    }

    @Override
    public void onClick(View view) {
        if(view == parseButton){
            parsePlaylistFile();
        }else if(view == playButton){
            playPlaylistItems();
        }else if(view == stopButton){
            stop();
        }
    }

    private void stop() {
        mediaPlayer.pause();
        playButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    private void playPlaylistItems() {
        playButton.setEnabled(false);
        currentPlaylistItemNumber = 0;
        if(playListItems.size() > 0){
            String path = ((PlaylistFile)playListItems.get(currentPlaylistItemNumber)).getFilePath();
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void parsePlaylistFile() {
        playListItems = new Vector();
        try {
            URL url = new URL(editTextUrl.getText().toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if(connection.getResponseCode() == 200){
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = bufferedReader.readLine()) != null){
                    Log.v("PLAYLISTLINE", line);
                    if(line.startsWith("#")){
                        //元數據，忽略
                    }else if(line.length() > 0){
                        String filePath = "";
                        if(line.startsWith("http://")){
                            filePath = line;
                        }else{
                            //假設是相對的
                            filePath = url.getPath()+line;
                        }
                        PlaylistFile playlistFile = new PlaylistFile();
                        playlistFile.setFilePath(filePath);
                        playListItems.add(playlistFile);
                    }
                }
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        playButton.setEnabled(true);
    }
}
