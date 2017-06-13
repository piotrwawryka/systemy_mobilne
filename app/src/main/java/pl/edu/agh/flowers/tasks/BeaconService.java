package pl.edu.agh.flowers.tasks;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.flowers.data.Injection;
import pl.edu.agh.flowers.data.Task;
import pl.edu.agh.flowers.data.source.TasksDataSource;
import pl.edu.agh.flowers.data.source.TasksRepository;
import pl.edu.agh.flowers.data.source.local.TimeDataDbHelper;

public class BeaconService extends Service implements BeaconConsumer {
    private static final String TAG = "BEACON_SERVICE";

    TasksRepository tasksRepository;
    BeaconManager beaconManager;
    TimeDataDbHelper timeDataDbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        tasksRepository = Injection.provideTasksRepository(getApplicationContext());
        beaconManager = BeaconManager.getInstanceForApplication(this);
        timeDataDbHelper = new TimeDataDbHelper(getApplicationContext());
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier((beacons, region) -> {
            tasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
                @Override
                public void onTasksLoaded(List<Task> tasks) {
                    for (Beacon beacon : beacons) {
                        for (Task task : tasksFor(tasks, beacon.getBluetoothAddress())) {
                            Log.i(TAG, "Found new value: " + beacon.getDistance() + " for flower: " + task.getId());
                            timeDataDbHelper.addElement(task.getId(), System.currentTimeMillis(), beacon.getDistance());
                        }
                    }
                }

                @Override
                public void onDataNotAvailable() {
                    // ignore beacon scanning
                }
            });
        });

        try {
            beaconManager.setForegroundScanPeriod(5000);
            beaconManager.setForegroundBetweenScanPeriod(0);
            beaconManager.startRangingBeaconsInRegion(new Region("FLOWERS_APP", null, null, null));
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
        }
    }

    private List<Task> tasksFor(List<Task> tasks, String bluetoothAddress) {
        final ArrayList<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getBeaconBluetoothAddress().equals(bluetoothAddress))
                filteredTasks.add(task);
        }
        return filteredTasks;
    }

}
