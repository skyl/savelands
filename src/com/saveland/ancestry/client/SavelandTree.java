package com.saveland.ancestry.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.maps.client.LoadApi;
import com.google.gwt.maps.client.LoadApi.LoadLibrary;
import com.google.gwt.user.client.ui.RootPanel;

public class SavelandTree implements EntryPoint {

    @Override
    public void onModuleLoad() {
        loadMapApi();
    }

    private void realOnModuleLoad() {
        RootPanel rPanel = RootPanel.get();
        ClientData cData = new ClientData();
        // cData is passed in, part Observable, part Global
        PersonForm pForm = new PersonForm(cData);
        InfoWindowMapWidget map = new InfoWindowMapWidget(cData);

        rPanel.add(map);
        // gets shown in a dialog, we hope.
        // rPanel.add(pForm);
        cData.addObserver(pForm);
        cData.addObserver(map);
        cData.fetchPeople();
    }

    private void loadMapApi() {
        boolean sensor = true;
        // load all the libs for use in the maps
        ArrayList<LoadLibrary> loadLibraries = new ArrayList<LoadApi.LoadLibrary>();
        loadLibraries.add(LoadLibrary.ADSENSE);
        loadLibraries.add(LoadLibrary.DRAWING);
        loadLibraries.add(LoadLibrary.GEOMETRY);
        loadLibraries.add(LoadLibrary.PANORAMIO);
        loadLibraries.add(LoadLibrary.PLACES);
        loadLibraries.add(LoadLibrary.WEATHER);
        loadLibraries.add(LoadLibrary.VISUALIZATION);
        Runnable onLoad = new Runnable() {
            @Override
            public void run() {
                realOnModuleLoad();
            }
        };
        LoadApi.go(onLoad, loadLibraries, sensor);
    }

}
