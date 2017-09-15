package xyz.tracestudios.gimmee;

import android.content.Context;
import android.content.ContextWrapper;

import java.io.Serializable;

public class SerializedContext extends ContextWrapper implements Serializable {

    Context context;

    public SerializedContext(Context context) {
        super(context);
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
