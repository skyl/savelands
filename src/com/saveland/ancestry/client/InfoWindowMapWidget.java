package com.saveland.ancestry.client;

/*
 * #%L
 * GWT Maps API V3 - Showcase
 * %%
 * Copyright (C) 2011 - 2012 GWT Maps API V3
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

//import com.google.gwt.core.client.GWT;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapTypeId;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.controls.ControlPosition;
import com.google.gwt.maps.client.events.MouseEvent;
import com.google.gwt.maps.client.events.click.ClickMapEvent;
import com.google.gwt.maps.client.events.click.ClickMapHandler;
import com.google.gwt.maps.client.events.tiles.TilesLoadedMapEvent;
import com.google.gwt.maps.client.events.tiles.TilesLoadedMapHandler;
import com.google.gwt.maps.client.overlays.InfoWindow;
import com.google.gwt.maps.client.overlays.InfoWindowOptions;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.maps.client.overlays.MarkerOptions;
import com.google.gwt.user.client.ui.Button;
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.saveland.ancestry.client.models.Person;

public class InfoWindowMapWidget extends Composite implements ClientDataObserver {

	private VerticalPanel pWidget;
	private MapWidget mapWidget;

	private ClientData cData;

	InfoWindowOptions options = InfoWindowOptions.newInstance();
	InfoWindow iw;

	public InfoWindowMapWidget(ClientData cd) {
		cData = cd;
		pWidget = new VerticalPanel();
		initWidget(pWidget);
		draw();
	}

	private void draw() {
		pWidget.clear();
		drawMap();
		drawControls();
	}

	protected void drawInfoWindow(String key, final Marker marker, MouseEvent mouseEvent) {
		if (marker == null || mouseEvent == null) {
			return;
		}

		if (iw != null) {
			iw.close();
		}

		Person person = (Person) cData.peopleKeyMap.get(key);

		HTML html = new HTML(
				person.getDisplayName()
				);
		VerticalPanel vp = new VerticalPanel();
		vp.add(html);

		options.setContent(vp);
		iw = InfoWindow.newInstance(options);
		iw.open(mapWidget, marker);

		// If you want to clear widgets, Use options.clear() to remove the widgets
		// from map
		// options.clear();
	}

	private void drawMap() {
		LatLng center = LatLng.newInstance(40, -98);
		MapOptions opts = MapOptions.newInstance();
		opts.setZoom(4);
		opts.setCenter(center);
		opts.setMapTypeId(MapTypeId.HYBRID);

		mapWidget = new MapWidget(opts);
		pWidget.add(mapWidget);
		pWidget.setSize("100%", "100%");
		mapWidget.setSize("100%", "100%");

		mapWidget.addClickHandler(new ClickMapHandler() {
			public void onEvent(ClickMapEvent event) {
				//GWT.log("clicked on latlng=" + event.getMouseEvent().getLatLng());
			}
		});

		mapWidget.addTilesLoadedHandler(new TilesLoadedMapHandler() {
			public void onEvent(TilesLoadedMapEvent event) {
				// Load something after the tiles load
			}
		});
	}

	private void drawControls() {
		Button button = new Button("+ Add New Person");
		button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				GWT.log("Clicked Add");
				cData.setCurrentPerson(null);
			}
		});

		FlowPanel widget = new FlowPanel();
		widget.add(button);
		//widget.add(new HTML("Custom Controls"));
		widget.addStyleName("map-controls");

		// TODO I'm not able to get the stylesheet to work, but this works below
		//DOM.setStyleAttribute(widget.getElement(), "background", "white");
		//DOM.setStyleAttribute(widget.getElement(), "padding", "5px");
		//DOM.setStyleAttribute(widget.getElement(), "margin", "3px");
		//DOM.setStyleAttribute(widget.getElement(), "border", "3px solid #FF0000");

		mapWidget.setControls(ControlPosition.RIGHT_CENTER, widget);
	}

	private void addPersonToMap(Person person) {
		final String key = person.key;
		LatLng center = LatLng.newInstance(person.lat, person.lon);
		MarkerOptions options = MarkerOptions.newInstance();
		options.setPosition(center);
		options.setTitle(person.getDisplayName());

		final Marker marker = Marker.newInstance(options);
		marker.setMap(mapWidget);

		marker.addClickHandler(new ClickMapHandler() {
			public void onEvent(ClickMapEvent event) {
				drawInfoWindow(key, marker, event.getMouseEvent());
				// fill the form with the person data
				cData.setCurrentPerson(key);
			}
		});
	}

	@Override
	public void onFetchPeopleSuccess() {
		for (Person person : cData.people) {
			addPersonToMap(person);
		}
	}

	@Override
	public void onFetchPeopleFailure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSavePersonSuccess(Person person) {
		// TODO what if it is an update?
		addPersonToMap(person);
	}

	@Override
	public void onSavePersonFailure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetCurrentPerson() {
		// Maybe we don't need to do anything here?
		// We just clicked on the map and filled the form ...
		// TODO Auto-generated method stub
		if (cData.currentPerson.key == null) {
			GWT.log("onSetCurrentPerson null closing IW");
			iw.close();
		}
	}
}
