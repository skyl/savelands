package com.saveland.ancestry.client;

import java.util.ArrayList;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DefaultDateTimeFormatInfo;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.saveland.ancestry.client.models.Person;

public class PersonForm extends DialogBox implements ClientDataObserver {

	private ClientData cData;
	//private Person person;

	ArrayList<Widget> errors = new ArrayList<Widget>();
	ArrayList<Widget> formWidgetsList = new ArrayList<Widget>();

	// main widget
	private VerticalPanel vp = new VerticalPanel();

	// must get these by ajax
	private MultiWordSuggestOracle motherO = new MultiWordSuggestOracle();
	private MultiWordSuggestOracle fatherO = new MultiWordSuggestOracle();
	private SuggestBox fatherSelect;
	private SuggestBox motherSelect;

	private Button closeButton;
	private TextBox firstName;
	private TextBox lastName;
	private HorizontalPanel gender;
	private RadioButton male;
	private RadioButton female;
	private TextBox birthDate;
	private AutocompletePlacesMapWidget wMap;
	private TextArea notes;
	private Button submitButton;

	public PersonForm(ClientData cd) {
		setWidget(this.vp);
		cData = cd;
	}

	private void attachUI() {
		closeButton = new Button();
		closeButton.setText("X Close");
		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//PersonForm.this.hide();
				PersonForm.this.clearForm();
			}
		});

		// firstName
		firstName = new TextBox();
		firstName.getElement().setPropertyString("placeholder", "First Name");
		firstName.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (firstName.getValue() == "") {
					addError(firstName);
				} else {
					removeError(firstName);
				}
			}
		});

		// lastName
		lastName = new TextBox();
		lastName.getElement().setPropertyString("placeholder", "Last Name");
		lastName.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (lastName.getValue() == "") {
					addError(lastName);
				} else {
					removeError(lastName);
				}
			}
		});

		// gender
		male = new RadioButton("gender", "male");
		female = new RadioButton("gender", "female");
		gender = new HorizontalPanel();
		gender.add(male);
		gender.add(female);
		// birthDate
		Label bdLabel = new Label("Date Born");
		birthDate = new TextBox();
		birthDate.getElement().setPropertyString("placeholder", "YYYY-MM-DD");
		// father / mother select styling
		fatherSelect.getElement().setPropertyString("placeholder", "Father's Name");
		motherSelect.getElement().setPropertyString("placeholder", "Mother's Name");
		// map
		wMap = new AutocompletePlacesMapWidget();
		// notes
		notes = new TextArea();
		notes.getElement().setPropertyString("placeholder", "notes");
		// POST button
		submitButton = new Button("Add!");
		// add the elements

		vp.add(firstName);
		vp.add(lastName);
		vp.add(gender);
		vp.add(bdLabel);
		vp.add(birthDate);
		vp.add(fatherSelect);
		vp.add(motherSelect);
		vp.add(wMap);
		vp.add(notes);
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(submitButton);
		hp.add(closeButton);
		vp.add(hp);
	}

	private void addHandlers() {
		submitButton.addClickHandler(new BtClickHandler());
	}

	private class BtClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			GWT.log("Button CLICK!");
			if (!isValid()) {
				GWT.log("NOT VALID");
				return;
			}
			cData.savePerson();
		}
	}

	// TODO: Bean validation .. meh ..
	// http://www.gwtproject.org/doc/latest/DevGuideValidation.html
	private Boolean isValid() {
		// removes any existing errors ...
		errors = new ArrayList<Widget>();

		if (cData.currentPerson == null) {
			// the currentPerson becomes a new Person here
			cData.setCurrentPerson(null);
		}
		Person person = cData.currentPerson;

		person.firstName = firstName.getValue();
		if (person.firstName == "") {
			addError(firstName);
		}
		person.lastName = lastName.getValue();
		if (person.lastName == "") {
			addError(lastName);
		}
		person.gender = male.getValue();
		if (!(male.getValue() || female.getValue())) {
			addError(gender);
		}

		// GWT can't use SimpleDateFormat.
		String pattern = "yyyy-MM-dd";
		DefaultDateTimeFormatInfo info = new DefaultDateTimeFormatInfo();
		DateTimeFormat dtf = new DateTimeFormat(pattern, info) {};  // <= trick here
		try {
			person.birthDate = dtf.parse(birthDate.getValue());
			GWT.log("birthdate parsed");
		} catch (IllegalArgumentException e) {
			GWT.log("date catch add errors");
			addError(birthDate);
		}

		String fs = fatherSelect.getValue();
		String ms = motherSelect.getValue();
		if (fs != "") {
			person.fatherKey = ((Person) cData.peopleMap.get(fs)).key;
			if (person.fatherKey == null) {
				errors.add(fatherSelect);
			}
		}
		if (ms != "") {
			person.motherKey = ((Person) cData.peopleMap.get(ms)).key;
			if (person.motherKey == null) {
				errors.add(motherSelect);
			}
		}

		person.lat = (float) wMap.latlng.getLatitude();
		person.lon = (float) wMap.latlng.getLongitude();

		person.notes = notes.getValue();

		if (errors.size() > 0) {
			GWT.log("returning errors!");
			return false;
		} else {
			return true;
		}
	}

	private void addError(Widget w) {
		w.addStyleName("error");
		errors.add(w);
	}

	private void removeError(Widget w) {
		w.removeStyleName("error");
		errors.remove(w);
	}

	private void clearForm() {
		firstName.setValue("");
		lastName.setValue("");
		male.setValue(false);
		female.setValue(false);
		birthDate.setValue("");
		fatherSelect.setValue("");
		motherSelect.setValue("");
		wMap.setValue("");
		notes.setValue("");
		this.hide();
		// this opens up the new Person dialog ..
		//cData.setCurrentPerson(null);
	}

	@Override
	public void onFetchPeopleSuccess() {
		// only gets called on startup?
		for (Person person : cData.people) {
			//GWT.log(person.toString());
			if (person.gender) {
				fatherO.add(person.getDisplayName());
			} else {
				motherO.add(person.getDisplayName());
			}
			//GWT.log(person.getDisplayName());
			//GWT.log(person.toString());
		}
		fatherSelect = new SuggestBox(fatherO);
		motherSelect = new SuggestBox(motherO);
		attachUI();
		addHandlers();
	}

	@Override
	public void onFetchPeopleFailure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSavePersonSuccess(Person person) {
		if (person.gender) {
			fatherO.add(person.getDisplayName());
		} else {
			motherO.add(person.getDisplayName());
		}
		clearForm();
	}

	@Override
	public void onSavePersonFailure() {
		// TODO Auto-generated method stub

	}


	@Override
	public void onSetCurrentPerson() {
		GWT.log("PersonForm onSetCurrentPerson");
		if (cData.currentPerson.key == null) {
			//Person person = new Person();
			setText("Add New Person");
			// Enable animation.
			setAnimationEnabled(true);
			// Enable glass background.
			setGlassEnabled(true);
			this.show();
			this.wMap.mapWidget.setSize("360px", "360px");
			this.wMap.mapWidget.triggerResize();
			return;
		}

		// we have a person, so let's fill the form with them
		// and open the dialog.
		Person person = cData.currentPerson;
		firstName.setValue(person.firstName);
		lastName.setValue(person.lastName);
		if (person.gender) {
			male.setValue(true);
		} else {
			female.setValue(true);
		}

		DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
		// prints Monday, December 17, 2007 in the default locale
		//GWT.log(fmt.format(today));
		// TODO format to YYYY-MM-DD ...
		birthDate.setValue(fmt.format(person.birthDate));
		wMap.setLatLng(person.lat, person.lon);

		notes.setValue(person.notes);
		submitButton.setText("Update!");

		// DialogBox methods
		setText("Edit " + cData.currentPerson.getDisplayName());
		// Enable animation.
		setAnimationEnabled(true);
		// Enable glass background.
		setGlassEnabled(true);
		this.show();
		this.wMap.mapWidget.setSize("360px", "360px");
		this.wMap.mapWidget.triggerResize();
	}

}
