package org.buildmlearn.toolkit.service;

/**
 * Created by iit2014094 on 2/19/2017.
 */

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.events.CompletionEvent;
import com.google.android.gms.drive.events.DriveEventService;

import org.buildmlearn.toolkit.R;


import static org.buildmlearn.toolkit.activity.HomeActivity.mGoogleApiClient;
import static org.buildmlearn.toolkit.fragment.LoadProjectFragment.semaphore;

/**
 * Created by iit2014094 on 2/15/2017.
 */

public class MyDriveEventService extends DriveEventService {

    /**
     * oncompletion method is called upon completion of sync of each apk/project or on failure of sync
     */
    @Override
    public void onCompletion(CompletionEvent event) {
        Log.d("TAG", "Action completed with status: " + event.getStatus());

        if (event.getStatus() == CompletionEvent.STATUS_SUCCESS) {

            DriveId driveId = event.getDriveId();
            DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, driveId);
            DriveResource.MetadataResult result = file.getMetadata(mGoogleApiClient).await();
            if (!result.getStatus().isSuccess()) {

                semaphore++;
                event.dismiss();
                return;
            }
            Metadata metadata = result.getMetadata();

            Log.d("TAGGG","Metadata succesfully fetched. Title: " + metadata.getTitle());

            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.activated_background)
                            .setSound(uri)
                            .setContentTitle(metadata.getTitle())
                            .setContentText("Successfully synced with drive");

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify((int) (Math.random()*255), mBuilder.build());
            semaphore++;

        }

        if (event.getStatus() == CompletionEvent.STATUS_FAILURE) {



            DriveId driveId = event.getDriveId();
            DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, driveId);
            DriveResource.MetadataResult result = file.getMetadata(mGoogleApiClient).await();
            if (!result.getStatus().isSuccess()) {
                event.dismiss();
                return;
            }
            Metadata metadata = result.getMetadata();

            Log.d("TAGGG","Metadata succesfully fetched. Title: " + metadata.getTitle());

            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.activated_background)
                            .setSound(uri)
                            .setContentTitle(metadata.getTitle())
                            .setContentText("failed to sync with drive");

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify((int) (Math.random()*255), mBuilder.build());

            semaphore ++;
            return ;

        }

        event.dismiss();
    }
}
