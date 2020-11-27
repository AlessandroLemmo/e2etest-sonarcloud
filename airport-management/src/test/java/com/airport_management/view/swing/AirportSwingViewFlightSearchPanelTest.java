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
public class AirportSwingViewFlightSearchPanelTest extends AssertJSwingJUnitTestCase {

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
		window.show(); // shows the frame to test	
	}
	
	
	
	@Test @GUITest
	public void testShowAllFoundedFlightsByOriginShouldAddFlightDescriptionsToTheListAndResetErrorLabel() {
		window.button(JButtonMatcher.withText("Flight Search")).click();

		Calendar cal = Calendar.getInstance();
		Date departureDate = cal.getTime();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date arrivalDate = cal.getTime();

		Plane plane1 = new Plane("id1", "model1");
		Plane plane2 = new Plane("id2", "model2");	
		Flight flight1 = new Flight("num1", departureDate, arrivalDate, "origin1", "destination1", plane1);
		Flight flight2 = new Flight("num2", departureDate, arrivalDate, "origin2", "destination2", plane2);

		GuiActionRunner.execute(
			() -> airportSwingView.showAllFoundedFlightsByOrigin(
					Arrays.asList(flight1, flight2))
		);
		
		String[] listContents = window.list("searchOriginList").contents();
		assertThat(listContents)
			.containsExactly("flight_num=num1, departure_date=" + departureDate + ", arrival_date=" + arrivalDate + ", origin=origin1, desination=destination1, plane[id=id1, model=model1]", 
							 "flight_num=num2, departure_date=" + departureDate + ", arrival_date=" + arrivalDate + ", origin=origin2, desination=destination2, plane[id=id2, model=model2]");
		window.label("errorSearchFlightLabel").requireText(" ");
	}
	
	
	
	@Test @GUITest
	public void testShowAllFoundedFlightsByDestinationShouldAddFlightDescriptionsToTheListAndResetErrorLabel() {
		window.button(JButtonMatcher.withText("Flight Search")).click();

		Calendar cal = Calendar.getInstance();
		Date departureDate = cal.getTime();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date arrivalDate = cal.getTime();

		Plane plane1 = new Plane("id1", "model1");
		Plane plane2 = new Plane("id2", "model2");	
		Flight flight1 = new Flight("num1", departureDate, arrivalDate, "origin1", "destination1", plane1);
		Flight flight2 = new Flight("num2", departureDate, arrivalDate, "origin2", "destination2", plane2);

		GuiActionRunner.execute(
			() -> airportSwingView.showAllFoundedFlightsByDestination(
					Arrays.asList(flight1, flight2))
		);
		
		String[] listContents = window.list("searchDestinationList").contents();
		assertThat(listContents)
			.containsExactly("flight_num=num1, departure_date=" + departureDate + ", arrival_date=" + arrivalDate + ", origin=origin1, desination=destination1, plane[id=id1, model=model1]", 
							 "flight_num=num2, departure_date=" + departureDate + ", arrival_date=" + arrivalDate + ", origin=origin2, desination=destination2, plane[id=id2, model=model2]");
		window.label("errorSearchFlightLabel").requireText(" ");
	}
	
	
	
	@Test @GUITest
	public void testShowAllFoundedFlightsByDepartureDateShouldAddFlightDescriptionsToTheListAndResetErrorLabel() {
		window.button(JButtonMatcher.withText("Flight Search")).click();

		Calendar cal = Calendar.getInstance();
		Date departureDate = cal.getTime();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date arrivalDate = cal.getTime();

		Plane plane1 = new Plane("id1", "model1");
		Plane plane2 = new Plane("id2", "model2");	
		Flight flight1 = new Flight("num1", departureDate, arrivalDate, "origin1", "destination1", plane1);
		Flight flight2 = new Flight("num2", departureDate, arrivalDate, "origin2", "destination2", plane2);

		GuiActionRunner.execute(
			() -> airportSwingView.showAllFoundedFlightsByDepartureDate(
					Arrays.asList(flight1, flight2))
		);
		
		String[] listContents = window.list("searchDepartureDateList").contents();
		assertThat(listContents)
			.containsExactly("flight_num=num1, departure_date=" + departureDate + ", arrival_date=" + arrivalDate + ", origin=origin1, desination=destination1, plane[id=id1, model=model1]", 
							 "flight_num=num2, departure_date=" + departureDate + ", arrival_date=" + arrivalDate + ", origin=origin2, desination=destination2, plane[id=id2, model=model2]");
		window.label("errorSearchFlightLabel").requireText(" ");
	}
	
	
	
	@Test @GUITest
	public void testShowAllFoundedFlightsByArrivalDateShouldAddFlightDescriptionsToTheListAndResetErrorLabel() {
		window.button(JButtonMatcher.withText("Flight Search")).click();

		Calendar cal = Calendar.getInstance();
		Date departureDate = cal.getTime();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date arrivalDate = cal.getTime();

		Plane plane1 = new Plane("id1", "model1");
		Plane plane2 = new Plane("id2", "model2");	
		Flight flight1 = new Flight("num1", departureDate, arrivalDate, "origin1", "destination1", plane1);
		Flight flight2 = new Flight("num2", departureDate, arrivalDate, "origin2", "destination2", plane2);

		GuiActionRunner.execute(
			() -> airportSwingView.showAllFoundedFlightsByArrivalDate(
					Arrays.asList(flight1, flight2))
		);
		
		String[] listContents = window.list("searchArrivalDateList").contents();
		assertThat(listContents)
			.containsExactly("flight_num=num1, departure_date=" + departureDate + ", arrival_date=" + arrivalDate + ", origin=origin1, desination=destination1, plane[id=id1, model=model1]", 
							 "flight_num=num2, departure_date=" + departureDate + ", arrival_date=" + arrivalDate + ", origin=origin2, desination=destination2, plane[id=id2, model=model2]");
		window.label("errorSearchFlightLabel").requireText(" ");
	}
	
	
	
	@Test @GUITest
	public void testShowSearchFlightErrorShouldShowTheMessageInTheErrorLabel() {
		window.button(JButtonMatcher.withText("Flight Search")).click();

		GuiActionRunner.execute(
			() -> airportSwingView.showSearchFlightError("error message")
		);
		window.label("errorSearchFlightLabel")
			.requireText("error message");
	}
	
	
	
	@Test @GUITest
	public void testClearListSearchByOriginShouldClearList() {
		window.button(JButtonMatcher.withText("Flight Search")).click();
		
		Calendar cal = Calendar.getInstance();
		Date departureDate = cal.getTime();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date arrivalDate = cal.getTime();

		Plane plane1 = new Plane("id1", "model1");
		Plane plane2 = new Plane("id2", "model2");	
		Flight flight1 = new Flight("num1", departureDate, arrivalDate, "origin1", "destination1", plane1);
		Flight flight2 = new Flight("num2", departureDate, arrivalDate, "origin2", "destination2", plane2);

		GuiActionRunner.execute(
			() -> airportSwingView.showAllFoundedFlightsByOrigin(
					Arrays.asList(flight1, flight2))
		);
		
		GuiActionRunner.execute(
			() -> airportSwingView.clearListSearchByOrigin()
		);
		
		String[] listContents = window.list("searchOriginList").contents();
		assertThat(listContents).isEmpty();
	}

	
	
	@Test @GUITest
	public void testClearListSearchByDestinationShouldClearList() {
		window.button(JButtonMatcher.withText("Flight Search")).click();
		
		Calendar cal = Calendar.getInstance();
		Date departureDate = cal.getTime();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date arrivalDate = cal.getTime();

		Plane plane1 = new Plane("id1", "model1");
		Plane plane2 = new Plane("id2", "model2");	
		Flight flight1 = new Flight("num1", departureDate, arrivalDate, "origin1", "destination1", plane1);
		Flight flight2 = new Flight("num2", departureDate, arrivalDate, "origin2", "destination2", plane2);

		GuiActionRunner.execute(
			() -> airportSwingView.showAllFoundedFlightsByDestination(
					Arrays.asList(flight1, flight2))
		);
		
		GuiActionRunner.execute(
			() -> airportSwingView.clearListSearchByDestination()
		);
		
		String[] listContents = window.list("searchDestinationList").contents();
		assertThat(listContents).isEmpty();
	}
	
	
	
	@Test @GUITest
	public void testClearListSearchByDepartureDateShouldClearList() {
		window.button(JButtonMatcher.withText("Flight Search")).click();
		
		Calendar cal = Calendar.getInstance();
		Date departureDate = cal.getTime();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date arrivalDate = cal.getTime();

		Plane plane1 = new Plane("id1", "model1");
		Plane plane2 = new Plane("id2", "model2");	
		Flight flight1 = new Flight("num1", departureDate, arrivalDate, "origin1", "destination1", plane1);
		Flight flight2 = new Flight("num2", departureDate, arrivalDate, "origin2", "destination2", plane2);

		GuiActionRunner.execute(
			() -> airportSwingView.showAllFoundedFlightsByDepartureDate(
					Arrays.asList(flight1, flight2))
		);
		
		GuiActionRunner.execute(
			() -> airportSwingView.clearListSearchByDepartureDate()
		);
		
		String[] listContents = window.list("searchDepartureDateList").contents();
		assertThat(listContents).isEmpty();
	}
	
	
	
	@Test @GUITest
	public void testClearListSearchByArrivalDateShouldClearList() {
		window.button(JButtonMatcher.withText("Flight Search")).click();
		
		Calendar cal = Calendar.getInstance();
		Date departureDate = cal.getTime();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date arrivalDate = cal.getTime();

		Plane plane1 = new Plane("id1", "model1");
		Plane plane2 = new Plane("id2", "model2");	
		Flight flight1 = new Flight("num1", departureDate, arrivalDate, "origin1", "destination1", plane1);
		Flight flight2 = new Flight("num2", departureDate, arrivalDate, "origin2", "destination2", plane2);

		GuiActionRunner.execute(
			() -> airportSwingView.showAllFoundedFlightsByArrivalDate(
					Arrays.asList(flight1, flight2))
		);
		
		GuiActionRunner.execute(
			() -> airportSwingView.clearListSearchByArrivalDate()
		);
		
		String[] listContents = window.list("searchArrivalDateList").contents();
		assertThat(listContents).isEmpty();
	}
	
	
	
	@Test @GUITest
	public void testSearchByOriginButtonShouldDelegateToSearchControllerFindAllFlightsByOrigin() {
		window.button(JButtonMatcher.withText("Flight Search")).click();
		String origin = "origin";
		window.textBox("searchOriginTextBox").enterText(origin);
		window.button(JButtonMatcher.withText("Search by origin")).click();
		verify(searchController).findAllFlightsByOrigin(origin);
	}
	
	
	
	@Test @GUITest
	public void testSearchByDestinationButtonShouldDelegateToSearchControllerFindAllFlightsByDestination() {
		window.button(JButtonMatcher.withText("Flight Search")).click();
		String destination = "destination";
		window.textBox("searchDestinationTextBox").enterText(destination);
		window.button(JButtonMatcher.withText("Search by destination")).click();
		verify(searchController).findAllFlightsByDestination(destination);
	}
	
	
	
	@Test @GUITest
	public void testSearchByDepartureDateButtonShouldDelegateToSearchControllerFindAllFlightsWithDepartureDateInRange() {
		window.button(JButtonMatcher.withText("Flight Search")).click();
		Date start = (Date)airportSwingView.getSpinnerSearchByDepartureDateStart().getValue();
		Date end = (Date)airportSwingView.getSpinnerSearchByDepartureDateEnd().getValue();
		window.button(JButtonMatcher.withText("Search by departure date")).click();
		verify(searchController).findAllFlightsWithDepartureDateInRange(start, end);
	}
	
	
	
	@Test @GUITest
	public void testSearchByArrivalDateButtonShouldDelegateToSearchControllerFindAllFlightsWithArrivalDateInRange() {
		window.button(JButtonMatcher.withText("Flight Search")).click();
		Date start = (Date)airportSwingView.getSpinnerSearchByArrivalDateStart().getValue();
		Date end = (Date)airportSwingView.getSpinnerSearchByArrivalDateEnd().getValue();
		window.button(JButtonMatcher.withText("Search by arrival date")).click();
		verify(searchController).findAllFlightsWithArrivalDateInRange(start, end);
	}
}


