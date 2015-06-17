package de.entrecode.datamanager_java_sdk.example;

import de.entrecode.datamanager_java_sdk.DataManager;
import de.entrecode.datamanager_java_sdk.Model;
import de.entrecode.datamanager_java_sdk.exceptions.ECMalformedDataManagerIDException;
import de.entrecode.datamanager_java_sdk.listener.ECErrorListener;
import de.entrecode.datamanager_java_sdk.model.ECEntry;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Simon Scherzinger, entrecode GmbH, Stuttgart (Germany) on 08.06.15.
 */
public class RunningExample {
    public static void main(String[] args) throws ECMalformedDataManagerIDException {
        // Error Listener for ecErrors
        ECErrorListener errorListener = error -> System.out.println(error.stringify());

        DataManager dataManagerReadOnly = new DataManager("f84710b8", true);

        dataManagerReadOnly.modelList()
                .onResponse(response -> {
                    for (Model model : response) {
                        System.out.println("Model: " + model.getModelID());
                    }
                })
                .onError(errorListener).go();

        dataManagerReadOnly.model("to-do-item").getSchema().forMethod("post")
                .onResponse(schema -> {
                    System.out.println(schema.toString());
                })
                .onError(errorListener).go();

        dataManagerReadOnly.model("to-do-item").entries()
                .onResponse(entriesList -> {
                    System.out.println(entriesList.getCount() + ":" + entriesList.getTotal());
                    for (ECEntry entry : entriesList.getEmbedded()) {
                        System.out.println("Entry: " + entry.get("id"));
                    }
                })
                .onError(errorListener)
                .filter(new HashMap<String, String>() {{
                    put("done", "true");
                }}).go();

        dataManagerReadOnly.register()
                .onResponse(user -> {
                    System.out.println("User from register: " + user.get("id"));
                    dataManagerReadOnly.setToken(UUID.fromString(String.valueOf(user.get("temporaryToken"))));
                    dataManagerReadOnly.setReadOnly(false);
                    dataManagerReadOnly.user((String) user.get("id"))
                            .onResponse(userFromID -> {
                                System.out.println("User from ID: " + userFromID.get("id"));
                            })
                            .onError(errorListener).go();
                })
                .onError(errorListener).go();

        DataManager dataManagerRW = new DataManager("f84710b8", UUID.fromString("e63dca99-6a56-43a5-8864-1a63ee8565e7"));

        dataManagerRW.model("to-do-item").entries()
                .onResponse(entriesList -> {
                    System.out.println(entriesList.getCount() + ":" + entriesList.getTotal());
                    for (ECEntry entry : entriesList.getEmbedded()) {
                        System.out.println("Entry: " + entry.get("id"));
                    }
                })
                .onError(errorListener).go();

        ECEntry newEntry = new ECEntry();
        newEntry.set("todo-text", "Test text");
        newEntry.set("done", false);
        dataManagerRW.model("to-do-item").createEntry(newEntry)
                .onResponse(entry -> {
                    System.out.println("Entry: " + entry.get("id"));
                    entry.delete()
                            .onResponse(response -> {
                                System.out.println("Deleted.");
                            })
                            .onError(errorListener).go();

                })
                .onError(errorListener).go();

        dataManagerRW.model("to-do-item").entry("4kmswCbFI")
                .onResponse(entry -> {
                    entry.set("done", true);
                    entry.save()
                            .onResponse(savedEntry -> {
                                System.out.println("Saved Entry: " + savedEntry.get("id"));
                                System.out.println("Done State: " + savedEntry.get("done"));
                            })
                            .onError(errorListener).go();
                })
                .onError(errorListener).go();

        dataManagerRW.getFileURL("b05e55b0-a8e7-4ed6-8cba-e6162acd53e5")
                .onResponse(url -> {
                    System.out.println("File: " + url);
                })
                .onError(errorListener).go();

        dataManagerRW.getImageURL("b05e55b0-a8e7-4ed6-8cba-e6162acd53e5")
                .size(245)
                .onResponse(url -> {
                    System.out.println("Image: " + url);
                })
                .onError(errorListener).go();

        dataManagerRW.getImageThumbURL("b05e55b0-a8e7-4ed6-8cba-e6162acd53e5")
                .size(55)
                .onResponse(url -> {
                    System.out.println("Thumbnail: " + url);
                })
                .onError(errorListener).go();
    }
}