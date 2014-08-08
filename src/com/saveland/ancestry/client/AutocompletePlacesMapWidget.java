package com.saveland.ancestry.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapTypeId;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.base.LatLngBounds;
import com.google.gwt.maps.client.events.bounds.BoundsChangeMapEvent;
import com.google.gwt.maps.client.events.bounds.BoundsChangeMapHandler;
import com.google.gwt.maps.client.events.click.ClickMapEvent;
import com.google.gwt.maps.client.events.click.ClickMapHandler;
import com.google.gwt.maps.client.events.place.PlaceChangeMapEvent;
import com.google.gwt.maps.client.events.place.PlaceChangeMapHandler;
import com.google.gwt.maps.client.overlays.Animation;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.maps.client.overlays.MarkerOptions;
import com.google.gwt.maps.client.placeslib.Autocomplete;
import com.google.gwt.maps.client.placeslib.AutocompleteOptions;
import com.google.gwt.maps.client.placeslib.AutocompleteType;
import com.google.gwt.maps.client.placeslib.PlaceGeometry;
import com.google.gwt.maps.client.placeslib.PlaceResult;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
//import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * <br>
 * <br>
 * See <a href=
 * "https://developers.google.com/maps/documentation/javascript/layers.html#FusionTables"
 * >FusionTables API Doc</a>
 */
public class AutocompletePlacesMapWidget extends Composite {

	private VerticalPanel pWidget;
	public MapWidget mapWidget;
	private TextBox tbPlaces;
	private MarkerOptions options = MarkerOptions.newInstance();
	//options.setAnimation(Animation.DROP);
	private Marker markerDrop = Marker.newInstance(options);

	public LatLng latlng;

	public AutocompletePlacesMapWidget() {
		pWidget = new VerticalPanel();
		initWidget(pWidget);
		draw();
	}

	private void draw() {
		pWidget.clear();
		tbPlaces = new TextBox();
		tbPlaces.setWidth("350px");
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(tbPlaces);
		pWidget.add(hp);
		hp.setCellVerticalAlignment(tbPlaces, HasVerticalAlignment.ALIGN_BOTTOM);
		drawMap();
		drawAutoComplete();
	}

	private void drawMap() {
		latlng = LatLng.newInstance(40, -98);
		MapOptions opts = MapOptions.newInstance();
		opts.setZoom(4);
		opts.setCenter(latlng);
		opts.setMapTypeId(MapTypeId.TERRAIN);

		mapWidget = new MapWidget(opts);
		pWidget.add(mapWidget);

		mapWidget.addClickHandler(new ClickMapHandler() {
			@Override
			public void onEvent(ClickMapEvent event) {
				latlng = event.getMouseEvent().getLatLng();
				GWT.log("clicked on latlng=" + latlng);
				drawMarkerWithDropAnimation();
			}
		});
	}

	private void drawAutoComplete() {

		Element element = tbPlaces.getElement();

		AutocompleteType[] types = new AutocompleteType[2];
		types[0] = AutocompleteType.ESTABLISHMENT;
		types[1] = AutocompleteType.GEOCODE;

		AutocompleteOptions options = AutocompleteOptions.newInstance();
		options.setTypes(types);
		options.setBounds(mapWidget.getBounds());

		final Autocomplete autoComplete = Autocomplete.newInstance(element, options);

		autoComplete.addPlaceChangeHandler(new PlaceChangeMapHandler() {
			@Override
			public void onEvent(PlaceChangeMapEvent event) {

				PlaceResult result = autoComplete.getPlace();

				PlaceGeometry geomtry = result.getGeometry();
				latlng = geomtry.getLocation();
				drawMarkerWithDropAnimation();
				//GWT.log("place changed center=" + latlng);
			}
		});

		mapWidget.addBoundsChangeHandler(new BoundsChangeMapHandler() {
			@Override
			public void onEvent(BoundsChangeMapEvent event) {
				LatLngBounds bounds = mapWidget.getBounds();
				autoComplete.setBounds(bounds);
			}
		});
	}

	public void setLatLng(float lat, float lon) {
		latlng = LatLng.newInstance(lat, lon);
		drawMarkerWithDropAnimation();
		GWT.log("setLatLng!");
	}

	private void drawMarkerWithDropAnimation() {
		markerDrop.setAnimation(Animation.DROP);
		markerDrop.setMap(mapWidget);
		markerDrop.setPosition(latlng);
		mapWidget.panTo(latlng);
	}

	public void setValue(String s) {
		this.tbPlaces.setValue(s);
	}

}