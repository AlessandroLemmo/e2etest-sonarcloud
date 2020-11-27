package com.airport_management.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Date;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.airport_management.controller.FlightController;
import com.airport_management.controller.PlaneController;
import com.airport_management.controller.SearchController;
import com.airport_management.model.Flight;
import com.airport_management.model.Plane;


@RunWith(GUITestRunner.class)
public class AirportSwingViewFlightPanelTest extends AssertJSwingJUnitTestCase{

	private FrameFixture window;

	private AirportSwingView airportSwingView;

	@Mock
	private PlaneController planeController;
	
	@Mock
	private FlightController flightController;
	
	@Mock
	private SearchController searchController;
	
	private static final String ID_FIXTURE_1 = "id1-test";
	private static final String ID_FIXTURE_2 = "id2-test";
	private static final String MODEL_FIXTURE = "model-test";
	private static final String NUM_FIXTURE_1 = "num1-test";
	private static final String NUM_FIXTURE_2 = "num2-test";
	private static final String ORIGIN_FIXTURE = "origin-test";
	private static final String DESTINATION_FIXTURE = "destination-test";
	private static final Date DEPARTURE_DATE_FIXTURE = new Date();
	private static final Date ARRIVAL_DATE_FIXTURE = new Date();
	private static final Plane PLANE_FIXTURE_1 = new Plane(ID_FIXTURE_1, MODEL_FIXTURE);
	private static final Plane PLANE_FIXTURE_2 = new Plane(ID_FIXTURE_2, MODEL_FIXTURE);
	private static final Flight FLIGHT_FIXTURE_1 = new Flight(NUM_FIXTURE_1, DEPARTURE_DATE_FIXTURE, ARRIVAL_DATE_FIXTURE, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
	private static final Flight FLIGHT_FIXTURE_2 = new Flight(NUM_FIXTURE_2, DEPARTURE_DATE_FIXTURE, ARRIVAL_DATE_FIXTURE, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_2);
	
	
	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this); 
		
		GuiActionRunner.execute(() -> {
			airportSwingView = new AirportSwingView();
			airportSwingView.setAirportController(planeController, flightController, searchController);
			return airportSwingView;
		});
		window = new FrameFixture(robot(), airportSwingView);
		window.show(); 
	}
	

	
	@Test @GUITest
	public void testWhenAllIsNotEmptyThenAddButtonShouldBeEnabled() {
		window.button(JButtonMatcher.withText("Flight Panel")).click();

		GuiActionRunner.execute(
				() -> airportSwingView.getComboBox().addItem("item"));
		
		window.panel("panel2").textBox("originTextBox").enterText("origin");		
		window.panel("panel2").textBox("destinationTextBox").enterText("destination");
		window.panel("panel2").button(JButtonMatcher.withText("Add")).requireEnabled();
	}
	
	
	
	@Test @GUITest
	public void testWhenOneAttributeIsBlankThenAddButtonShouldBeDisabled() {
		
		window.button(JButtonMatcher.withText("Flight Panel")).click();
		
		JTextComponentFixture originTextBox = window.panel("panel2").textBox("originTextBox");
		JTextComponentFixture destinationTextBox = window.panel("panel2").textBox("destinationTextBox");
		JButtonFixture addButton = window.panel("panel2").button(JButtonMatcher.withText("Add"));
		
		originTextBox.enterText("origin");		
		destinationTextBox.enterText("destination");
		addButton.requireDisabled();
		
		originTextBox.setText("");
		destinationTextBox.setText("");
		
		GuiActionRunner.execute(
				() -> airportSwingView.getComboBox().addItem("item"));
		originTextBox.enterText("origin");		
		destinationTextBox.enterText(" ");
		addButton.requireDisabled();

		originTextBox.setText("");
		destinationTextBox.setText("");
		
		originTextBox.enterText(" ");		
		destinationTextBox.enterText("destination");
		addButton.requireDisabled();
		
		originTextBox.setText("");
		destinationTextBox.setText("");
		
		originTextBox.enterText("origin");		
		destinationTextBox.enterText("destination");
		addButton.requireEnabled();
	}
	
	
	
	@Test @GUITest
	public void testDeleteButtonShouldBeEnabledOnlyWhenAFlightIsSelected() {
		window.button(JButtonMatcher.withText("Flight Panel")).click();

		GuiActionRunner.execute(
				() -> airportSwingView.getListFlightsModel().addElement(new Flight()));

		window.panel("panel2").list("flightsList").selectItem(0);
		JButtonFixture deleteButton = window.panel("panel2").button(JButtonMatcher.withText("Delete Selected"));
		deleteButton.requireEnabled();
		window.panel("panel2").list("flightsList").clearSelection();
		deleteButton.requireDisabled();
	}
	
	
	
	@Test @GUITest
	public void testsShowAllFlightsShouldAddFlightDescriptionsToTheList() {
		window.button(JButtonMatcher.withText("Flight Panel")).click();
		
		GuiActionRunner.execute(
			() -> airportSwingView.showAllFlights(
					Arrays.asList(FLIGHT_FIXTURE_1, FLIGHT_FIXTURE_2))
		);
		
		String[] listContents = window.panel("panel2").list().contents();
		assertThat(listContents)
			.containsExactly(
					"flight_num=" + NUM_FIXTURE_1 + 
					", departure_date=" + DEPARTURE_DATE_FIXTURE + 
					", arrival_date=" + ARRIVAL_DATE_FIXTURE + 
					", origin=" + ORIGIN_FIXTURE + 
					", desination=" + DESTINATION_FIXTURE + 
					", plane[id=" + ID_FIXTURE_1 + 
					", model=" + MODEL_FIXTURE + "]", 
							 
					"flight_num=" + NUM_FIXTURE_2 + 
					", departure_date=" + DEPARTURE_DATE_FIXTURE + 
					", arrival_date=" + ARRIVAL_DATE_FIXTURE + 
					", origin=" + ORIGIN_FIXTURE + 
					", desination=" + DESTINATION_FIXTURE + 
					", plane[id=" + ID_FIXTURE_2 + 
					", model=" + MODEL_FIXTURE + "]");					
	}
	
	
	
	@Test @GUITest
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		window.button(JButtonMatcher.withText("Flight Panel")).click();

		GuiActionRunner.execute(
			() -> airportSwingView.showFlightError("error message")
		);
		window.label("errorMessageLabel")
			.requireText("error message");
	}
	
	
	
	@Test @GUITest
	public void testPlaneAddedShouldAddThePlaneToTheListAndResetTheErrorLabel() {
		window.button(JButtonMatcher.withText("Flight Panel")).click();
		
		GuiActionRunner.execute(
				() ->
				airportSwingView.flightAdded(FLIGHT_FIXTURE_1));
		
		String[] listContents = window.panel("panel2").list().contents();
		assertThat(listContents).containsExactly(
				"flight_num=" + NUM_FIXTURE_1 + 
				", departure_date=" + DEPARTURE_DATE_FIXTURE + 
				", arrival_date=" + ARRIVAL_DATE_FIXTURE + 
				", origin=" + ORIGIN_FIXTURE + 
				", desination=" + DESTINATION_FIXTURE + 
				", plane[id=" + ID_FIXTURE_1 + 
				", model=" + MODEL_FIXTURE + "]");
		window.label("errorMessageLabel").requireText(" ");
	}
	
	
	
	@Test @GUITest
	public void testPlaneRemovedShouldRemoveThePlaneFromTheListAndResetTheErrorLabel() {
		// setup
		window.button(JButtonMatcher.withText("Flight Panel")).click();
		GuiActionRunner.execute(
			() -> {
				DefaultListModel<Flight> listFlightsModel = airportSwingView.getListFlightsModel();
				listFlightsModel.addElement(FLIGHT_FIXTURE_1);
				listFlightsModel.addElement(FLIGHT_FIXTURE_2);
			}
		);
		
		// execute
		GuiActionRunner.execute(
			() ->
			airportSwingView.flightRemoved(FLIGHT_FIXTURE_1)
		);
		
		// verify
		String[] listContents = window.panel("panel2").list().contents();
		assertThat(listContents).containsExactly(
				"flight_num=" + NUM_FIXTURE_2 + 
				", departure_date=" + DEPARTURE_DATE_FIXTURE + 
				", arrival_date=" + ARRIVAL_DATE_FIXTURE + 
				", origin=" + ORIGIN_FIXTURE + 
				", desination=" + DESTINATION_FIXTURE + 
				", plane[id=" + ID_FIXTURE_2 + 
				", model=" + MODEL_FIXTURE + "]");
		window.label("errorMessageLabel").requireText(" ");
	}
	
	
	
	@Test @GUITest
	public void testAddButtonShouldDelegateToFlightControllerNewFlight() {
		window.button(JButtonMatcher.withText("Flight Panel")).click();
		Date departureDate = (Date)airportSwingView.getSpinnerDepartureDate().getValue();
		Date arrivalDate = (Date)airportSwingView.getSpinnerArrivalDate().getValue();
		
		GuiActionRunner.execute(
				() -> airportSwingView.getComboBox().addItem("item"));
		
		window.panel("panel2").textBox("originTextBox").enterText("origin");	
		window.panel("panel2").textBox("destinationTextBox").enterText("destination");
		window.panel("panel2").button(JButtonMatcher.withText("Add")).click();
		verify(flightController).newFlight(new Flight(departureDate, arrivalDate, "origin", "destination", null));
	}

	
	
	@Test
	public void testDeleteButtonShouldDelegateToFlightControllerDeleteFlight() {
		window.button(JButtonMatcher.withText("Flight Panel")).click();
		
		GuiActionRunner.execute(
			() -> {
				DefaultListModel<Flight> listFlightsModel = airportSwingView.getListFlightsModel();
				listFlightsModel.addElement(FLIGHT_FIXTURE_1);
				listFlightsModel.addElement(FLIGHT_FIXTURE_2);
			}
		);
		
		window.panel("panel2").list("flightsList").selectItem(1);
		window.panel("panel2").button(JButtonMatcher.withText("Delete Selected")).click();
		verify(flightController).deleteFlight(FLIGHT_FIXTURE_2);
	}	
}


