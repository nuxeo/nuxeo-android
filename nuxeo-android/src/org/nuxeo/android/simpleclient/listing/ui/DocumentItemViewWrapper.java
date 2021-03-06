/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */
package org.nuxeo.android.simpleclient.listing.ui;

import org.nuxeo.android.simpleclient.R;
import org.nuxeo.android.simpleclient.listing.BaseObjectListActivity;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.smartnsoft.droid4me.framework.DetailsProvider.BusinessViewWrapper;
import com.smartnsoft.droid4me.framework.DetailsProvider.ObjectEvent;

public final class DocumentItemViewWrapper extends
        BusinessViewWrapper<Document> {

    protected final BaseObjectListActivity baseObjectListActivity;

    public DocumentItemViewWrapper(
            BaseObjectListActivity baseDocumentListActivity,
            Document businessObject) {
        super(businessObject);
        this.baseObjectListActivity = baseDocumentListActivity;
    }

    @Override
    protected View createNewView(Activity activity, Document businessObject) {
        return activity.getLayoutInflater().inflate(
                R.layout.my_documents_document, null);
    }

    @Override
    protected Object extractNewViewAttributes(Activity activity, View view,
            Document businessObject) {
        return new DocumentItemAttributesUpdater(view);
    }

    @Override
    protected void updateView(Activity activity, Object viewAttributes,
            View view, Document businessObject, int position) {
        ((ObjectItemViewUpdater) viewAttributes).update(activity,
                this.baseObjectListActivity.getHandler(), businessObject);
    }

    @Override
    public Intent computeIntent(Activity activity, Object viewAttributes,
            View view, Document doc, ObjectEvent objectEvent) {
        Intent intent = this.baseObjectListActivity.handleEventOnListItem(
                activity, viewAttributes, view, doc, objectEvent);
        if (intent != null) {
            return intent;
        }
        return this.computeIntent(activity, view, objectEvent);
    }
}