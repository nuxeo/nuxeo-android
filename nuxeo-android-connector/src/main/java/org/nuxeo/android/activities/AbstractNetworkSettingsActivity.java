package org.nuxeo.android.activities;

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;
import org.nuxeo.android.network.NuxeoNetworkStatus;
import org.nuxeo.ecm.automation.client.cache.DeferredUpdateManager;
import org.nuxeo.ecm.automation.client.cache.ResponseCacheManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

public abstract class AbstractNetworkSettingsActivity extends BaseNuxeoActivity {

    protected BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctx, Intent intent) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					updateOfflineDisplay(getNuxeoContext().getNetworkStatus());
				}
			});
		}
    };

	@Override
	protected void onResume() {
		registerReceiver(receiver, new IntentFilter(NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED));
		refreshAll();
		super.onResume();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}

	protected void resetNetworkStatus(final Runnable afterReset) {
		Runnable tester = new Runnable() {
			@Override
			public void run() {
				getNuxeoContext().getNetworkStatus().reset();
				if (afterReset!=null) {
					runOnUiThread(afterReset);
				}
			}
		};
		new Thread(tester).start();
	}

	protected void refreshAll() {
		updateOfflineDisplay(getNuxeoContext().getNetworkStatus());
		updateCacheInfoDisplay(getNuxeoContext().getResponseCacheManager(), getNuxeoContext().getDeferredUpdatetManager());
	}

	protected void resetNetworkStatusAndRefresh() {
		resetNetworkStatus(new Runnable() {
			@Override
			public void run() {
				refreshAll();
			}
		});
	}

	protected void executePendingUpdates() {
		DeferredUpdateManager dum = getNuxeoContext().getDeferredUpdatetManager();
		if (dum.getPendingRequestCount()>0) {
			dum.executePendingRequests(getNuxeoSession(), new Handler() {
				@Override
				public void handleMessage(Message msg) {
					refreshAll();
					super.handleMessage(msg);
				}
			});
		}
	}

	protected abstract void updateOfflineDisplay(NuxeoNetworkStatus settings);

	protected abstract void updateCacheInfoDisplay(ResponseCacheManager cacheManager, DeferredUpdateManager deferredUpdateManager);

	protected void flushResponseCache() {
		getNuxeoContext().getResponseCacheManager().clear();
		updateCacheInfoDisplay(getNuxeoContext().getResponseCacheManager(), getNuxeoContext().getDeferredUpdatetManager());
	}

	protected void flushDefferedUpdateManager() {
		getNuxeoContext().getDeferredUpdatetManager().purgePendingUpdates();
		updateCacheInfoDisplay(getNuxeoContext().getResponseCacheManager(), getNuxeoContext().getDeferredUpdatetManager());
	}


	protected void goOffline(boolean offline) {
		getNuxeoContext().getNetworkStatus().setForceOffline(offline);
		updateOfflineDisplay(getNuxeoContext().getNetworkStatus());
	}
}