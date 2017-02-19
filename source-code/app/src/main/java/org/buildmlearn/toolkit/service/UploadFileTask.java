package org.buildmlearn.toolkit.service;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;

import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.ExecutionOptions;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.buildmlearn.toolkit.activity.HomeActivity.mGoogleApiClient;

/**
 * Created by iit2014094 on 2/20/2017.
 */

public class UploadFileTask  extends AsyncTask<String,Void,Void>{

    private DriveFile driveFile;
    private String path,nameOfFile;


    @Override
    protected Void doInBackground(String... strings) {

        nameOfFile = strings[0];
        path   =strings[3];


        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE,strings[0]))
                .build();

        Drive.DriveApi.query(mGoogleApiClient, query)
                .setResultCallback(metadataCallback);

        return null;
    }

    /**
     * Handles whether to create a new   apk/saved project/draft   in the google drive or update the already persent apk/saved project/draft
     */
    private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback =
            new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {
                    if (!metadataBufferResult.getStatus().isSuccess()) {
                        Log.d("TAG","No internet connection");
                        return ;
                    }

                    int results = metadataBufferResult.getMetadataBuffer().getCount();

                    //if apk /saved project/draft is already present in drive
                    if (results > 0) {

                        DriveId driveId = metadataBufferResult.getMetadataBuffer().get(0).getDriveId();
                        driveFile = driveId.asDriveFile();

                        //open the drivefile  and call to result callback to update it
                        driveFile.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null)
                                .setResultCallback(driveContentsCallback);

                    }
                    //if apk / saved file/draft is not present in the drive
                    else if(results == 0){

                        //call to result callback to create new apk /new saved project/new draft in the drive
                        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                                .setResultCallback(newContentsCallback);
                    }
                }
            };

    /**
     *  updates the already exisiting apk/saved project/draft in the drive
     */

    private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {
                    if (!driveContentsResult.getStatus().isSuccess()) {
                        driveFile.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null)
                                .setResultCallback(driveContentsCallback);
                        return;
                    }

                    OutputStream outputStream = driveContentsResult.getDriveContents().getOutputStream();

                    try {

                        File tempFile = new File(path);
                        byte[] bytefile =readFile(tempFile);
                        outputStream.write(bytefile);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //get notification after completion of sync of apk/saved project/draft
                    ExecutionOptions executionOptions = new ExecutionOptions.Builder()
                            .setNotifyOnCompletion(true)
                            .build();

                    //sync the apk/saved project/draft with the drive
                    driveContentsResult.getDriveContents().commit(mGoogleApiClient, null, executionOptions)
                            .setResultCallback(new ResultCallback<com.google.android.gms.common.api.Status>() {
                                @Override
                                public void onResult(@NonNull com.google.android.gms.common.api.Status status) {
                                    Log.d("TAG","onresult callback recieved");

                                }
                            });

                }

                /**
                 * converts the given file data into byte array and returns it
                 */

                private byte[] readFile(File file) throws IOException {

                    ByteArrayOutputStream ous = null;
                    InputStream ios = null;
                    try {
                        byte[] buffer = new byte[400096];
                        ous = new ByteArrayOutputStream();
                        ios = new FileInputStream(file);
                        int read;
                        while ((read = ios.read(buffer)) != -1) {
                            ous.write(buffer, 0, read);
                        }
                    } finally {
                        try {
                            if (ous != null)
                                ous.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            if (ios != null)
                                ios.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return ous.toByteArray();
                }
            };

    /**
     *  creates new apk/draft/saved project in the drive and initializes its contents to null
     *
     */

    private ResultCallback<DriveApi.DriveContentsResult> newContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {

                    if (!driveContentsResult.getStatus().isSuccess()) {
                        return;
                    }

                    Log.d("TAG","sucessful recieving of drive contents");


                    //providing metadata for the new apk /saved project/draft
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(nameOfFile)
                            .setMimeType("*/*")
                            .build();


                    //create the new apk /saved project/draft  without data
                    Drive.DriveApi.getRootFolder(mGoogleApiClient)
                            .createFile(mGoogleApiClient, changeSet, null)
                            .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                @Override
                                public void onResult(@NonNull DriveFolder.DriveFileResult driveFileResult) {
                                    Log.d("TAG","New File Succesfully created");
                                }
                            });


                    //build the query for the apk /saved project/draft
                    Query query = new Query.Builder()
                            .addFilter(Filters.eq(SearchableField.TITLE,nameOfFile))

                            .build();

                    //query the drive again for the apk/draft/saved project
                    Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(metadataCallback);
                }
            };
}
