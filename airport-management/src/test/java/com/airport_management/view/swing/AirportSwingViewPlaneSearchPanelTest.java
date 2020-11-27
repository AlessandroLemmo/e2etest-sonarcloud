package com.airport_management.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
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
public class AirportSwingViewPlaneSearchPanelTest  extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private AirportSwingView airportSwingView;

	@Mock
	private PlaneController planeController;
	
	@Mock
	private FlightController flightController;
	
	@Mock
	private SearchController searchController;
	
	
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
	public void testShowAllFoundedFlightsAssociatesWithPlaneShouldAddFlightDescriptionsToTheListAndResetErrorLabel() {
		window.button(JButtonMatcher.withText("Plane Search")).click();

		Calendar cal = Calendar.getInstance();
		Date departureDate = cal.getTime();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date arrivalDate = cal.getTime();

		Plane plane = new Plane("id", "model");
		Flight flight1 = new Flight("num1", departureDate, arrivalDate, "origin1", "destination1", plane);
		Flight flight2 = new Flight("num2", departureDate, arrivalDate, "origin2", "destination2", plane);
		
		GuiActionRunner.execute(
			() -> airportSwingView.showAllFoundedFlightsAssociatesWithPlane(
					Arrays.asList(flight1, flight2))
		);
		
		String[] listContents = window.list("searchFlightsAssociatesList").contents();
		assertThat(listContents)
			.containsExactly("flight_num=num1, departure_date=" + departureDate + ", arrival_date=" + arrivalDate + ", origin=origin1, desination=destination1, plane[id=id, model=model]", 
							 "flight_num=num2, departure_date=" + departureDate + ", arrival_date=" + arrivalDate + ", origin=origin2, desination=destination2, plane[id=id, model=model]");
		window.label("errorSearchPlaneLabel").requireText(" ");
	}
	
	
	
	@Test @GUITest
	public void testShowAllFoundedPlanesByModelShouldAddFlightDescriptionsToTheListAndResetErrorLabel() {
		window.button(JButtonMatcher.withText("Plane Search")).click();
		Plane plane1 = new Plane("id1", "model");
		Plane plane2 = new Plane("id2", "model");	
		
		GuiActionRunner.execute(
			() -> airportSwingView.showAllFoundedPlanesByModel(
					Arrays.asList(plane1, plane2))
		);
		
		String[] listContents = window.list("searchPlaneByModelList").contents();
		assertThat(listContents)
			.containsExactly("id=id1, model=model", 
							 "id=id2, model=model");
		window.label("errorSearchPlaneLabel").requireText(" ");
	}
	
	
	
	@Test @GUITest
	public void testShowSearchPlaneErrorShouldShowTheMessageInTheErrorLabel() {
		window.button(JButtonMatcher.withText("Plane Search")).click();

		GuiActionRunner.execute(
			() -> airportSwingView.showSearchPlaneError("error message")
		);
		window.label("errorSearchPlaneLabel")
			.requireText("error message");
	}
	
	
	
	@Test @GUITest
	public void testClearListSearchAssociatesFlightsShouldClearList() {
		window.button(JButtonMatcher.withText("Plane Search")).click();
		
		Calendar cal = Calendar.getInstance();
		Date departureDate = cal.getTime();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date arrivalDate = cal.getTime();

		Plane plane = new Plane("id", "model");
		Flight flight1 = new Flight("num1", departureDate, arrivalDate, "origin1", "destination1", plane);
		Flight flight2 = new Flight("num2", departureDate, arrivalDate, "origin2", "destination2", plane);

		GuiActionRunner.execute(
			() -> airportSwingView.showAllFoundedFlightsAssociatesWithPlane(
					Arrays.asList(flight1, flight2))
		);
		
		GuiActionRunner.execute(
			() -> airportSwingView.clearListSearchAssociatesFlights()
		);
		
		String[] listContents = window.list("searchFlightsAssociatesList").contents();
		assertThat(listContents).isEmpty();
	}
	
	
	
	@Test @GUITest
	public void testClearListSearchByModelShouldClearList() {
		window.button(JButtonMatcher.withText("Plane Search")).click();
		Plane plane1 = new Plane("id1", "model");
		Plane plane2 = new Plane("id2", "model");

		GuiActionRunner.execute(
			() -> airportSwingView.showAllFoundedPlanesByModel(
					Arrays.asList(plane1, plane2))
		);
		
		GuiActionRunner.execute(
			() -> airportSwingView.clearListSearchByModel()
		);
		
		String[] listContents = window.list("searchPlaneByModelList").contents();
		assertThat(listContents).isEmpty();
	}
	
	
	
	@Test @GUITest
	public void testSearchAssociatesFlightsButtonShouldDelegateToSearchControllerFindAllFlightsAssiociatesWithPlane() {
		window.button(JButtonMatcher.withText("Plane Search")).click();
		String planeId = "planeId";
		
		GuiActionRunner.execute(
				() -> airportSwingView.getComboBoxSearch().addItem(planeId));
		
		window.button(JButtonMatcher.withText("Search associates flights")).click();
		verify(searchController).findAllFlightsAssiociatesWithPlane(planeId);
	}

	
	@Test @GUITest
	public void testSearchByModelButtonShouldDelegateToSearchControllerFindAllPlanesByModel() {
		window.button(JButtonMatcher.withText("Plane Search")).click();
		String model = "model";
		window.textBox("searchModelTextBox").enterText(model);
		window.button(JButtonMatcher.withText("Search by model")).click();
		verify(searchController).findAllPlanesByModel(model);
	}
}
