package edu.incense.android.comm;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;

import edu.incense.android.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

/***
 * Uploader is used to upload/send a file to an specific server depending on the
 * type of file
 * 
 * @author mxpxgx
 * 
 */
public class Uploader extends Connection {
    private String serverDbAddress;
    private String serverSinkPath;
    private String serverSurveyPath;
    private String serverMediaPath;

    public Uploader(Context context) {
        super(context);
    }

    protected void setServersFromPreferences() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        serverDbAddress = sp.getString("editTextDbServerAddress",
                "http://urbanmoments.dyndns.org:8182");
    }

    @Override
    protected void setPathsFromResource() {
        super.setPathsFromResource();
        serverSinkPath = context.getResources().getString(
                R.string.server_sink_path);
        serverSurveyPath = context.getResources().getString(
                R.string.server_survey_path);
        serverMediaPath = context.getResources().getString(
                R.string.server_media_path);
    }

    public boolean postSinkData(String filePath) {
        if (!connect(serverDbAddress + serverSinkPath, ConnectionType.OUTPUT,
                JSON_TYPE))
            return false;
        return postData(filePath, false);
    }

    public boolean postAudioData(String filePath) {
        return postMediaData(filePath, AUDIO_3GP_TYPE);
    }

    private boolean postMediaData(String filePath, String contentType) {
        if (!connect(serverDbAddress + serverMediaPath, ConnectionType.OUTPUT,
                contentType))
            return false;
        return postData(filePath, true);
    }

    public boolean postSurveyData(String filePath) {
        if (!connect(serverDbAddress + serverSurveyPath, ConnectionType.OUTPUT,
                JSON_TYPE))
            return false;
        return postData(filePath, false);
    }

    // POST implemented with HttpURLConnection
    private boolean postData(String filePath, boolean isResource) {

        if (!isConnected()) {
            Log.i(getClass().getName(), "File transmistion failed.");
            return false;
        }

        try {

            // Set streams
            FileInputStream fis;
            if (!isResource) {
                File parent = new File(Environment.getExternalStorageDirectory(), parentDirectory);
                parent.mkdirs();
                File file = new File(parent, filePath);
                fis = new FileInputStream(file);
            } else {
                fis = context.openFileInput(filePath);
            }
            DataOutputStream dos = new DataOutputStream(
                    connection.getOutputStream());

            // POST
            // Read bytes until EOF to write
            int bytesAvailable = fis.available();
            int bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE);
            byte[] buffer = new byte[bufferSize];

            // How many bytes in buffer
            int bytes_read;
            while ((bytes_read = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytes_read);
            }

            // Close streams
            dos.flush();
            dos.close();
            fis.close();

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            Log.i("HTTP", serverResponseCode + " : " + serverResponseMessage);
            Log.i(getClass().getName(), "Succesful file transmistion.");
            return true;
        } catch (Exception e) {
            // Exception handling
            Log.i(getClass().getName(), "File transmistion failed.");
            Log.e(getClass().getName(), "File transmistion failed.", e);
            return false;
        } finally {
            disconnect();
            Log.i(getClass().getName(), "Disconnected after file transmition.");
        }
    }

}
