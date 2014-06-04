package com.axiastudio.menjazo;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * User: tiziano
 * Date: 04/06/14
 * Time: 19:24
 */
public class Menjazo {

    private String path=null;
    private String mimeType=null;
    private Repository repository;

    public static Menjazo create() {
        return new Menjazo();
    }

    public Menjazo open(String url, String user, String password){
        Map<String, String> parameter = new HashMap<>();
        parameter.put(SessionParameter.USER, user);
        parameter.put(SessionParameter.PASSWORD, password);
        parameter.put(SessionParameter.ATOMPUB_URL, url);
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        parameter.put(SessionParameter.AUTH_HTTP_BASIC, "true");
        parameter.put(SessionParameter.COOKIES, "true");
        parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");
        SessionFactoryImpl factory = SessionFactoryImpl.newInstance();
        repository = factory.getRepositories(parameter).get(0);
        return this;
    }

    public Menjazo path(String path){
        this.path = path;
        return this;
    }

    public Menjazo mime(String mime){
        mimeType = mime;
        return this;
    }

    public InputStream stream(){
        Session session = repository.createSession();
        CmisObject object = session.getObjectByPath(path);
        if( object instanceof Document ){
            Document document = (Document) object;
            return document.getContentStream().getStream();
        }
        return null;
    }

    public String fromStream(InputStream inputStream){
        String folderName = path.substring(0, path.lastIndexOf("/"));
        String name = path.substring(path.lastIndexOf("/")+1);
        Session session = repository.createSession();
        CmisObject object = session.getObjectByPath(folderName);
        if( object instanceof Folder ){
            Folder folder = (Folder) object;
            ContentStream contentStream = new ContentStreamImpl(name, null, mimeType, inputStream);
            Map<String, Object> properties = new HashMap<>();
            properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
            properties.put(PropertyIds.NAME, name);
            Document document = folder.createDocument(properties, contentStream, VersioningState.MAJOR);
            return document.getId();
        }
        return null;
    }
}
