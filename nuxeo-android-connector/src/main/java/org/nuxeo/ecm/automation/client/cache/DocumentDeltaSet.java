package org.nuxeo.ecm.automation.client.cache;

import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

public class DocumentDeltaSet {

	protected final OperationType opType;

	protected final PropertyMap dirtyProps;

	protected final String docType;

	protected final String path;

	protected final String id;

	protected final String requestId;

	protected final String listName;

	public DocumentDeltaSet(OperationType opType,String id, String path, String docType, PropertyMap dirtyProps, String requestId, String listName) {
		this.dirtyProps = dirtyProps;
		this.docType=docType;
		this.path = path;
		this.opType=opType;
		this.id=id;
		this.listName=listName;
		this.requestId=requestId;
	}

	public DocumentDeltaSet(OperationType opType,Document doc, String requestId, String listName) {
		this.dirtyProps = doc.getDirtyProperties();
		this.docType=doc.getType();
		this.path = doc.getPath();
		this.opType=opType;
		this.id = doc.getId();
		this.listName=listName;
		this.requestId=requestId;
	}

	public Document apply(Document doc) {
		if (opType== OperationType.CREATE) {
			return  new Document(id, path, docType, doc.getDirtyProperties());
		} else if (opType== OperationType.UPDATE) {
			if (doc!=null) {
				doc.getProperties().map().putAll(dirtyProps.map());
				doc.getDirtyFields().addAll(dirtyProps.map().keySet());
			}
		}
		return doc;
	}

	public OperationType getOperationType() {
		return opType;
	}

	public PropertyMap getDirtyProps() {
		return dirtyProps;
	}

	public String getDocType() {
		return docType;
	}

	public String getPath() {
		return path;
	}

	public String getId() {
		return id;
	}

	public String getRequestId() {
		return requestId;
	}

	public String getListName() {
		return listName;
	}
}
