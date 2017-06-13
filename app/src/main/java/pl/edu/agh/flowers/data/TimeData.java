package pl.edu.agh.flowers.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;

public class TimeData {
    @NonNull
    private final String mId;

    @NonNull
    private final String mTaskId;

    @NonNull
    private final Long mTimestamp;

    @NonNull
    private final Double mValue;

    public TimeData(@NonNull String mId, @NonNull String mTaskId, @NonNull Long mTimestamp, @NonNull Double mValue) {
        this.mId = mId;
        this.mTaskId = mTaskId;
        this.mTimestamp = mTimestamp;
        this.mValue = mValue;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mTaskId, mTimestamp);
    }

    @Override
    public String toString() {
        return "Time Data for task " + mTaskId + " with timestamp " + mTimestamp;
    }
}
